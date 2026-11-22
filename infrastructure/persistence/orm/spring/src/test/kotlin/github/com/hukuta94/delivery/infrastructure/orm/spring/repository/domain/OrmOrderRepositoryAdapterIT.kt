package github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.assignedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.completedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.createdOrder
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.infrastructure.orm.spring.integration.SpringJpaIntegrationSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

internal class OrmOrderRepositoryAdapterIT : SpringJpaIntegrationSpec() {
    init {
        val orderAdapter = OrmOrderRepositoryAdapter(context.getBean(OrderJpaRepository::class.java))
        val courierAdapter = OrmCourierRepositoryAdapter(context.getBean(CourierJpaRepository::class.java))

        "add then getById round-trips an order" {
            val order = createdOrder(location = location(2, 3))

            orderAdapter.add(order)

            val loaded = orderAdapter.getById(order.id)
            loaded.id shouldBe order.id
            loaded.location shouldBe order.location
            loaded.status shouldBe OrderStatus.CREATED
            loaded.courierId shouldBe null
        }

        "getAllNotCompleted returns CREATED and ASSIGNED, excludes COMPLETED" {
            val courier = freeCourier()
            courierAdapter.add(courier)

            val created = createdOrder(location = location(1, 1))
            val assigned = assignedOrder(location = location(2, 2), courier = courier)
            val completed = completedOrder(location = location(3, 3), courier = courier)
            orderAdapter.add(created)
            orderAdapter.add(assigned)
            orderAdapter.add(completed)

            val notCompletedIds = orderAdapter.getAllNotCompleted().map { it.id }

            notCompletedIds shouldContainExactlyInAnyOrder listOf(created.id, assigned.id)
        }
    }
}
