package github.com.hukuta94.delivery.infrastructure.orm.ktorm.query

import github.com.hukuta94.delivery.core.application.query.common.LocationResponse
import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.assignedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.completedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.createdOrder
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.integration.KtormIntegrationSpec
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.KtormCourierRepository
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.KtormOrderRepository
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

internal class KtormGetNotCompletedOrdersQueryIT : KtormIntegrationSpec() {
    init {
        "returns CREATED and ASSIGNED orders, excludes COMPLETED, and maps id/location" {
            // Given
            val courierRepository = KtormCourierRepository(database)
            val orderRepository = KtormOrderRepository(database)
            val sut = KtormGetNotCompletedOrdersQuery(database)

            val courier = freeCourier()
            courierRepository.add(courier)

            val created = createdOrder(location = location(1, 2))
            val assigned = assignedOrder(location = location(3, 4), courier = courier)
            val completed = completedOrder(location = location(5, 6), courier = courier)
            orderRepository.add(created)
            orderRepository.add(assigned)
            orderRepository.add(completed)

            // When
            val response = sut.execute()

            // Then
            val byId = response.orders.associateBy { it.id }
            byId.keys shouldContainExactlyInAnyOrder setOf(created.id, assigned.id)
            byId.getValue(created.id).location shouldBe LocationResponse(1, 2)
        }
    }
}
