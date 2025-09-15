package github.com.hukuta94.delivery.core.domain.aggregate.order

import github.com.hukuta94.delivery.core.domain.aggregate.courier.newCourier
import github.com.hukuta94.delivery.core.domain.common.newLocationWithRandomCoords
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
        val location = newLocationWithRandomCoords()

        // When
        val sut = Order.create(
            id = id,
            location = location,
        )

        // Then
        assertSoftly {
            sut.status shouldBe OrderStatus.CREATED
            sut.courierId shouldBe null
        }
    }

    "order status is changed when courier is assigned" {
        // Given
        val sut = newOrder()
        val courier = newCourier()

        // When
        sut.assignCourier(courier)

        // Then
        assertSoftly {
            sut.status shouldBe OrderStatus.ASSIGNED
            sut.courierId shouldBe courier.id
        }
    }

    "order can not be reassigned to other courier" {
        // Given
        val otherCourier = newCourier()
        val notReassignableOrders = listOf(
            newAssignedOrder(),
            newCompletedOrder(),
        )

        checkAll(
            iterations = notReassignableOrders.size,
            Arb.of(notReassignableOrders)
        ) { sut ->
            // When
            sut.assignCourier(otherCourier)

            // Then
            sut.courierId shouldNotBe otherCourier.id
        }
    }

    "order can be completed when courier is assigned" {
        // Given
        val sut = newAssignedOrder()

        // When
        sut.complete()

        // Then
        sut.status shouldBe OrderStatus.COMPLETED
    }

    "order can not be completed when courier is not assigned" {
        // Given
        val sut = newOrder()

        // When
        sut.complete()

        // Then
        sut.status shouldNotBe OrderStatus.COMPLETED
    }

    "raise order assigned domain event when order is assigned courier" {
        // Given
        val sut = newAssignedOrder()

        // When
        val actualDomainEvent = sut.popDomainEvents().last() as OrderAssignedDomainEvent

        // Then
        assertSoftly {
            actualDomainEvent::class shouldBe OrderAssignedDomainEvent::class
            actualDomainEvent.orderId shouldBe sut.id
            actualDomainEvent.courierId shouldBe sut.courierId
        }
    }

    "raise order completed domain event when order is completed" {
        // Given
        val sut = newCompletedOrder()

        // When
        val actualDomainEvent = sut.popDomainEvents().last() as OrderCompletedDomainEvent

        // Then
        assertSoftly {
            actualDomainEvent::class shouldBe OrderCompletedDomainEvent::class
            actualDomainEvent.orderId shouldBe sut.id
        }
    }
})
