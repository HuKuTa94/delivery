package github.com.hukuta94.delivery.core.domain.aggregate.order

import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.common.randomLocation
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.of
import io.kotest.property.checkAll
import java.util.*

class OrderTest : StringSpec({

    "new order is created in correct status and state" {
        // Given
        val id = UUID.randomUUID()
        val location = randomLocation()

        // When
        val order = Order.create(
            id = id,
            location = location,
        )

        // Then
        assertSoftly {
            order.status shouldBe OrderStatus.CREATED
            order.courierId shouldBe null
        }
    }

    "order status is changed when courier is assigned" {
        // Given
        val order = createdOrder()
        val courier = freeCourier()

        // When
        order.assignCourier(courier)

        // Then
        assertSoftly {
            order.status shouldBe OrderStatus.ASSIGNED
            order.courierId shouldBe courier.id
        }
    }

    "order can not be reassigned to other courier" {
        // Given
        val otherCourier = freeCourier()
        val notReassignableOrders = listOf(
            assignedOrder(),
            completedOrder(),
        )

        checkAll(
            iterations = notReassignableOrders.size,
            Arb.of(notReassignableOrders)
        ) { order ->
            // When
            order.assignCourier(otherCourier)

            // Then
            order.courierId shouldNotBe otherCourier.id
        }
    }

    "order can be completed when courier is assigned" {
        // Given
        val order = assignedOrder()

        // When
        order.complete()

        // Then
        order.status shouldBe OrderStatus.COMPLETED
    }

    "order can not be completed when courier is not assigned" {
        // Given
        val order = createdOrder()

        // When
        order.complete()

        // Then
        order.status shouldNotBe OrderStatus.COMPLETED
    }

    "raise order assigned domain event when order is assigned courier" {
        // Given
        val order = assignedOrder()

        // When
        val actualDomainEvent = order.popDomainEvents().last() as OrderAssignedDomainEvent

        // Then
        assertSoftly {
            actualDomainEvent::class shouldBe OrderAssignedDomainEvent::class
            actualDomainEvent.orderId shouldBe order.id
            actualDomainEvent.courierId shouldBe order.courierId
        }
    }

    "raise order completed domain event when order is completed" {
        // Given
        val order = completedOrder()

        // When
        val actualDomainEvent = order.popDomainEvents().last() as OrderCompletedDomainEvent

        // Then
        assertSoftly {
            actualDomainEvent::class shouldBe OrderCompletedDomainEvent::class
            actualDomainEvent.orderId shouldBe order.id
        }
    }
})
