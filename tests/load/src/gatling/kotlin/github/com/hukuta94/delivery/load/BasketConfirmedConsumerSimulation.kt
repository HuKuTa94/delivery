package github.com.hukuta94.delivery.load

import io.gatling.javaapi.core.CoreDsl.asLongAsDuring
import io.gatling.javaapi.core.CoreDsl.atOnceUsers
import io.gatling.javaapi.core.CoreDsl.constantUsersPerSec
import io.gatling.javaapi.core.CoreDsl.details
import io.gatling.javaapi.core.CoreDsl.exec
import io.gatling.javaapi.core.CoreDsl.pause
import io.gatling.javaapi.core.CoreDsl.rampUsersPerSec
import io.gatling.javaapi.core.CoreDsl.scenario
import io.gatling.javaapi.core.ScenarioBuilder
import io.gatling.javaapi.core.Session
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.http
import io.gatling.javaapi.http.HttpDsl.status
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime

private val baseUrl = System.getProperty("delivery.base-url")
private val kafkaBootstrapServers = System.getProperty("delivery.kafka.bootstrap-servers")
private val eventsPerSecond = System.getProperty("delivery.load.basket-events-per-second").toDouble()
private val steadyLoad = Duration.ofSeconds(System.getProperty("delivery.load.duration-seconds").toLong())

/**
 * Load test of the `basket.confirmed` Kafka consumer. The consumer only stores each event in the inbox (the inbox
 * relay creates the order later), so the test checks that it keeps up with a constant stream of events: every event
 * the broker acknowledged is in the inbox soon after the load.
 *
 * Open model: events arrive at a fixed rate whatever the consumer does, Kafka keeps the ones it hasn't read yet.
 * Every virtual user sends the event of a new basket without waiting for the broker. The rate grows from 1 event/s
 * to [eventsPerSecond] during [WARM_UP] (JIT, connection pools), then stays there for [steadyLoad]. The default rate,
 * 1000 events/s, is 40 times the peak of the HTTP entry point in [CreateOrderSimulation].
 *
 * SLO, it fails `gatlingRun` when broken: the broker acknowledged every event, and every acknowledged event is stored
 * in the inbox once within [CATCH_UP_TIMEOUT] after the load. The sends aren't Gatling requests, so the report shows
 * the users and the health check after the load only. The test prints the inbox throughput from the first to the last
 * stored event: the rate of the load while the consumer keeps up, its limit when it doesn't. Grafana shows the
 * consumer group lag during the run.
 */
class BasketConfirmedConsumerSimulation : Simulation() {

    private val producer = BasketConfirmedProducer(kafkaBootstrapServers)

    private val inbox = InboxMessages(
        url = System.getProperty("delivery.database.url"),
        user = System.getProperty("delivery.database.user"),
        password = System.getProperty("delivery.database.password"),
    )

    // Read when Gatling creates the simulation, before the first event: messages created after it are of this run
    private val newestMessageBeforeRun: LocalDateTime? = inbox.newestCreatedAt()

    private val sendEvent: ScenarioBuilder = scenario("Send basket.confirmed")
        .exec { session ->
            producer.send()
            session
        }

    // One user after the load: waits until the broker has answered every event and the consumer has stored the
    // acknowledged ones, then checks the SLO. A broken SLO marks the group as failed, so its assertion fails the run
    // after the reports are generated. The flush and the JDBC calls block a Gatling thread: fine, no other user
    // runs by then
    private val consumerCatchUp: ScenarioBuilder = scenario("Consumer catch-up")
        .group(CONSUMER_SLO_GROUP)
        .on(
            exec { session ->
                session
                    .set(SENT, producer.flush())
                    .set(LOAD_END, Instant.now())
            },
            asLongAsDuring({ session -> runMessages().messages < session.sent().acknowledged }, CATCH_UP_TIMEOUT)
                .on(pause(POLL_INTERVAL)),
            exec { session -> checkConsumerSlo(session) },
            // The one request of the run: Gatling crashes instead of generating reports for a run without requests.
            // It also checks that the application is up after the load
            exec(
                http("GET /actuator/health")
                    .get("/actuator/health")
                    .check(status().shouldBe(OK))
            ),
        )

    init {
        setUp(
            sendEvent
                .injectOpen(
                    rampUsersPerSec(1.0).to(eventsPerSecond).during(WARM_UP),
                    constantUsersPerSec(eventsPerSecond).during(steadyLoad),
                )
                .andThen(consumerCatchUp.injectOpen(atOnceUsers(1)))
        )
            .protocols(http.baseUrl(baseUrl))
            .assertions(
                details(CONSUMER_SLO_GROUP).failedRequests().count().shouldBe(0L),
            )
    }

    override fun after() {
        producer.close()
        inbox.close()
    }

    private fun runMessages(): StoredMessages = inbox.messagesCreatedAfter(newestMessageBeforeRun)

    private fun Session.sent(): SentEvents = checkNotNull(get<SentEvents>(SENT))

    private fun checkConsumerSlo(session: Session): Session {
        val sent = session.sent()
        val stored = runMessages()
        val waited = Duration.between(session.get<Instant>(LOAD_END), Instant.now())
        val result = "$sent; $stored; waited ${waited.toMillis()} ms after the load"
        return if (meetsSlo(sent, stored)) {
            println("Consumer SLO ($CONSUMER_SLO) met: $result")
            session
        } else {
            println("Consumer SLO ($CONSUMER_SLO) broken: $result")
            session.markAsFailed()
        }
    }

    // No acknowledged event means the broker is unreachable; more stored messages than acknowledged events means
    // the consumer stored some of them twice
    private fun meetsSlo(
        sent: SentEvents,
        stored: StoredMessages,
    ): Boolean = sent.acknowledged > 0 && sent.failed == 0 && stored.messages == sent.acknowledged

    private companion object {
        const val OK = 200
        const val CONSUMER_SLO_GROUP = "Consumer SLO"
        const val SENT = "sent"
        const val LOAD_END = "loadEnd"

        val WARM_UP: Duration = Duration.ofSeconds(10)
        val POLL_INTERVAL: Duration = Duration.ofMillis(500)
        val CATCH_UP_TIMEOUT: Duration = Duration.ofSeconds(10)
        val CONSUMER_SLO = "every acknowledged event stored in the inbox once within ${CATCH_UP_TIMEOUT.seconds} s " +
            "after the load, no failed send"
    }
}
