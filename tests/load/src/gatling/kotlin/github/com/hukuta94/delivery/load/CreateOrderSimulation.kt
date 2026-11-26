package github.com.hukuta94.delivery.load

import io.gatling.javaapi.core.CoreDsl.asLongAsDuring
import io.gatling.javaapi.core.CoreDsl.atOnceUsers
import io.gatling.javaapi.core.CoreDsl.constantUsersPerSec
import io.gatling.javaapi.core.CoreDsl.details
import io.gatling.javaapi.core.CoreDsl.exec
import io.gatling.javaapi.core.CoreDsl.global
import io.gatling.javaapi.core.CoreDsl.pause
import io.gatling.javaapi.core.CoreDsl.rampUsersPerSec
import io.gatling.javaapi.core.CoreDsl.scenario
import io.gatling.javaapi.core.ScenarioBuilder
import io.gatling.javaapi.core.Session
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.http
import io.gatling.javaapi.http.HttpDsl.status
import io.gatling.javaapi.http.HttpProtocolBuilder
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime

private val baseUrl: String = System.getProperty("delivery.base-url")
private val ordersPerSecond: Double = System.getProperty("delivery.load.orders-per-second").toDouble()
private val steadyLoad: Duration = Duration.ofSeconds(System.getProperty("delivery.load.duration-seconds").toLong())

/**
 * Load test of order creation through the HTTP entry point. `POST /api/v1/orders/` only stores a basket confirmed
 * event in the inbox, the inbox relay creates the order later. So the response time is the inbox write only, and
 * the test also checks the inbox lag: the time from storing the event to the order, read from the database.
 *
 * Open model: new clients arrive at a fixed rate whatever the response time is, as orders do in reality.
 * The rate grows from 1 order/s to [ordersPerSecond] during [WARM_UP] (JIT, connection pools), then stays there
 * for [steadyLoad]. The default rate, 25 orders/s, is a peak of 1000 orders/min × 1.5.
 *
 * SLOs, each one fails `gatlingRun` when broken (the HTTP ones cover the warm-up requests too):
 * - HTTP: no failed request, p99 response time < [HTTP_P99_MILLIS] ms;
 * - inbox: every message of the run is processed within [DRAIN_TIMEOUT] after the load, p99 lag < [INBOX_LAG_P99].
 */
class CreateOrderSimulation : Simulation() {

    private val inbox = InboxMessages(
        url = System.getProperty("delivery.database.url"),
        user = System.getProperty("delivery.database.user"),
        password = System.getProperty("delivery.database.password"),
    )

    // Read when Gatling creates the simulation, before the first request: messages created after it are of this run
    private val newestMessageBeforeRun: LocalDateTime? = inbox.newestCreatedAt()

    private val httpProtocol: HttpProtocolBuilder = http
        .baseUrl(baseUrl)
        .acceptHeader("application/json")

    private val createOrder: ScenarioBuilder = scenario("Create order")
        .exec(
            http("POST /api/v1/orders/")
                .post("/api/v1/orders/")
                .check(status().shouldBe(ACCEPTED))
        )

    // One user after the load: waits for the relay to process the messages of the run, then checks the inbox SLO.
    // A broken SLO marks the group as failed, so its assertion fails the run after the reports are generated
    // (crashLoadGenerator would stop Gatling before them). The wait counts in the run duration, so it lowers
    // the mean throughput of the report. The JDBC calls block a Gatling thread: fine, no other user runs by then
    private val inboxLag: ScenarioBuilder = scenario("Inbox lag")
        .group(INBOX_SLO_GROUP)
        .on(
            asLongAsDuring({ runInboxLag().unprocessed > 0 }, DRAIN_TIMEOUT).on(pause(POLL_INTERVAL)),
            exec { session -> checkInboxSlo(session) },
        )

    init {
        setUp(
            createOrder
                .injectOpen(
                    rampUsersPerSec(1.0).to(ordersPerSecond).during(WARM_UP),
                    constantUsersPerSec(ordersPerSecond).during(steadyLoad),
                )
                .andThen(inboxLag.injectOpen(atOnceUsers(1)))
        )
            .protocols(httpProtocol)
            .assertions(
                global().failedRequests().count().shouldBe(0L),
                global().responseTime().percentile(HTTP_PERCENTILE).lt(HTTP_P99_MILLIS),
                details(INBOX_SLO_GROUP).failedRequests().count().shouldBe(0L),
            )
    }

    // The relay processes the inbox in creation order, so messages left by earlier runs would delay this one
    override fun before() {
        var pending = inbox.pendingMessages()
        if (pending > 0) {
            println("Waiting for the relay to process $pending inbox messages left by earlier runs")
        }
        val deadline = Instant.now() + LEFTOVERS_TIMEOUT
        while (pending > 0 && Instant.now() < deadline) {
            Thread.sleep(POLL_INTERVAL)
            pending = inbox.pendingMessages()
        }
        check(pending == 0) {
            "$pending inbox messages left by earlier runs are not processed within ${LEFTOVERS_TIMEOUT.toMinutes()} min"
        }
    }

    override fun after() {
        inbox.close()
    }

    private fun runInboxLag(): InboxLag = inbox.lagOfMessagesCreatedAfter(newestMessageBeforeRun)

    private fun checkInboxSlo(session: Session): Session {
        val lag = runInboxLag()
        return if (lag.meetsSlo()) {
            println("Inbox SLO ($INBOX_SLO) met: $lag")
            session
        } else {
            println("Inbox SLO ($INBOX_SLO) broken: $lag")
            session.markAsFailed()
        }
    }

    // No messages means the app writes to another database than the one the test reads
    private fun InboxLag.meetsSlo(): Boolean =
        messages > 0 && unprocessed == 0 && p99 != null && p99 < INBOX_LAG_P99

    private companion object {
        // The order doesn't exist yet when the event is in the inbox
        const val ACCEPTED = 202
        const val INBOX_SLO_GROUP = "Inbox SLO"

        const val HTTP_PERCENTILE = 99.0
        const val HTTP_P99_MILLIS = 200

        val WARM_UP: Duration = Duration.ofSeconds(10)
        val POLL_INTERVAL: Duration = Duration.ofSeconds(1)
        val LEFTOVERS_TIMEOUT: Duration = Duration.ofMinutes(2)

        // The relay polls the inbox every 5 s, so a lag up to ~5 s is by design
        val DRAIN_TIMEOUT: Duration = Duration.ofSeconds(30)
        val INBOX_LAG_P99: Duration = Duration.ofSeconds(10)
        val INBOX_SLO = "all processed within ${DRAIN_TIMEOUT.seconds} s after the load, " +
            "p99 lag < ${INBOX_LAG_P99.seconds} s"
    }
}
