package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository

import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.assignedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.completedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.createdOrder
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.integration.KtormIntegrationSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

internal class KtormOrderRepositoryIT : KtormIntegrationSpec() {
    init {
        "add then getById round-trips an order" {
            val repository = KtormOrderRepository(database)
            val order = createdOrder(location = location(2, 3))

            repository.add(order)

            val loaded = repository.getById(order.id)
            loaded.id shouldBe order.id
            loaded.location shouldBe order.location
            loaded.status shouldBe OrderStatus.CREATED
            loaded.courierId shouldBe null
        }

        "status-based queries filter correctly" {
            val courierRepository = KtormCourierRepository(database)
            val orderRepository = KtormOrderRepository(database)
            val courier = freeCourier()
            courierRepository.add(courier)

            val created = createdOrder(location = location(1, 1))
            val assigned = assignedOrder(location = location(2, 2), courier = courier)
            val completed = completedOrder(location = location(3, 3), courier = courier)
            orderRepository.add(created)
            orderRepository.add(assigned)
            orderRepository.add(completed)

            val createdIds = orderRepository.getAllCreated().map { it.id }
            val assignedIds = orderRepository.getAllAssigned().map { it.id }
            val notCompletedIds = orderRepository.getAllNotCompleted().map { it.id }

            createdIds shouldContainExactlyInAnyOrder listOf(created.id)
            assignedIds shouldContainExactlyInAnyOrder listOf(assigned.id)
            notCompletedIds shouldContainExactlyInAnyOrder listOf(created.id, assigned.id)
        }
    }
}
