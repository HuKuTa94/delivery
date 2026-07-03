package github.com.hukuta94.delivery.infrastructure.orm.spring.query.order

import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.assignedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.completedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.createdOrder
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.infrastructure.orm.spring.integration.SpringJpaIntegrationSpec
import github.com.hukuta94.delivery.infrastructure.orm.spring.model.converter.LocationConverter
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain.CourierJpaRepository
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain.OrderJpaRepository
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain.OrmCourierRepositoryAdapter
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain.OrmOrderRepositoryAdapter
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

internal class GetNotCompletedOrdersQueryImplIT : SpringJpaIntegrationSpec() {
    init {
        val orderAdapter = OrmOrderRepositoryAdapter(context.getBean(OrderJpaRepository::class.java))
        val courierAdapter = OrmCourierRepositoryAdapter(context.getBean(CourierJpaRepository::class.java))
        val sut = GetNotCompletedOrdersQueryImpl(
            context.getBean(NamedParameterJdbcTemplate::class.java),
            LocationConverter(),
        )

        "returns only CREATED and ASSIGNED orders (excludes COMPLETED)" {
            val courier = freeCourier()
            courierAdapter.add(courier)
            val created = createdOrder(location = location(1, 2))
            val assigned = assignedOrder(location = location(3, 4), courier = courier)
            val completed = completedOrder(location = location(5, 6), courier = courier)
            orderAdapter.add(created)
            orderAdapter.add(assigned)
            orderAdapter.add(completed)

            val response = sut.execute()

            response.orders.map { it.id } shouldContainExactlyInAnyOrder listOf(created.id, assigned.id)
        }
    }
}
