package github.com.hukuta94.delivery.e2e

import github.com.hukuta94.delivery.infrastructure.kafka.OrderStatus
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.nondeterministic.eventuallyConfig
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

private val kafkaBootstrapServers: String = systemProperty("delivery.kafka.bootstrap-servers")

/**
 * How long each stage of the delivery may take. The slowest one is the courier's way to the order:
 * one step per second, up to 18 steps across the grid.
 */
private val STAGE = eventuallyConfig {
    duration = 1.minutes
    interval = 250.milliseconds
}

class OrderDeliveryE2ETest : StringSpec({

    // Created before the test sends anything, so the consumer reads every status change that follows
    val statusChanges = autoClose(OrderStatusChangedConsumer(kafkaBootstrapServers))
    val baskets = autoClose(BasketConfirmedProducer(kafkaBootstrapServers))
    val database = autoClose(
        DeliveryDatabase(
            url = systemProperty("delivery.database.url"),
            user = systemProperty("delivery.database.user"),
            password = systemProperty("delivery.database.password"),
        )
    )

    "confirmed basket is delivered by a courier who is free again afterwards" {
        // Given
        val basketId = UUID.randomUUID()

        // When
        baskets.send(basketId)

        // Then
        // the event went through the inbox: it was stored and processed
        eventually(STAGE) {
            database.inboxMessageStatus(basketId) shouldBe "SUCCESS"
        }
        // processing the event created the order, whose id is the basket id
        database.order(basketId).shouldNotBeNull()
        // a courier was assigned to the order
        val courierId = eventually(STAGE) {
            database.order(basketId)?.courier.shouldNotBeNull().id
        }
        // the courier reached the order: the order is completed and the courier is free again where the order was
        val completedOrder = eventually(STAGE) {
            database.order(basketId).shouldNotBeNull().also { it.status shouldBe "COMPLETED" }
        }
        assertSoftly(completedOrder.courier.shouldNotBeNull()) {
            id shouldBe courierId
            status shouldBe "FREE"
            location shouldBe completedOrder.location
        }
        // the order announced both of its status changes, in order
        val statuses = eventually(STAGE) {
            statusChanges.statusesOf(basketId).also { it shouldContain OrderStatus.Completed }
        }
        statuses shouldContainExactly listOf(OrderStatus.Assigned, OrderStatus.Completed)
    }
})

private fun systemProperty(name: String): String = System.getProperty(name)
    ?: error("System property $name is not set: run the tests with `./gradlew :tests:e2e:e2eTest`")
