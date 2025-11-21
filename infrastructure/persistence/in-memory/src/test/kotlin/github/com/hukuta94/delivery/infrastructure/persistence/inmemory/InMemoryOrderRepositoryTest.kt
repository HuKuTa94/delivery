package github.com.hukuta94.delivery.infrastructure.persistence.inmemory

import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.assignedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.completedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.createdOrder
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import java.util.*

class InMemoryOrderRepositoryTest : StringSpec({

    lateinit var sut: InMemoryOrderRepository

    beforeTest {
        sut = InMemoryOrderRepository()
    }

    "can add the order" {
        // Given
        val order = createdOrder()

        // When
        sut.add(order)

        // Then
        val actual = sut.getById(order.id)
        actual shouldBeSameInstanceAs order
    }

    "can update the order" {
        // Given
        val orderId = UUID.randomUUID()
        val orderInStorage = createdOrder(
            id = orderId,
        )
        val orderToUpdate = completedOrder(
            id = orderId,
            location = orderInStorage.location,
        )
        sut.add(orderInStorage)

        // When
        sut.update(orderToUpdate)
        val actual = sut.getById(orderId)

        // Then
        assertSoftly {
            actual shouldNotBeSameInstanceAs orderInStorage
            actual.id shouldBe orderId
            actual.location shouldBe orderInStorage.location
            actual.status shouldNotBe orderInStorage.status
            actual.status shouldBe OrderStatus.COMPLETED
        }
    }

    "can get all created orders" {
        // Given
        val countOfCreatedOrders = 3
        repeat(countOfCreatedOrders) {
            sut.add(createdOrder())
        }
        val assignedOrder = assignedOrder()
        sut.add(assignedOrder)

        // When
        val actual = sut.getAllCreated()

        // Then
        actual.size shouldBe countOfCreatedOrders
    }

    "can get all assigned orders" {
        // Given
        val countOfAssignedOrders = 3
        repeat(countOfAssignedOrders) {
            sut.add(assignedOrder())
        }
        val createdOrder = createdOrder()
        sut.add(createdOrder)

        // When
        val actual = sut.getAllAssigned()

        // Then
        actual.size shouldBe countOfAssignedOrders
    }
})
