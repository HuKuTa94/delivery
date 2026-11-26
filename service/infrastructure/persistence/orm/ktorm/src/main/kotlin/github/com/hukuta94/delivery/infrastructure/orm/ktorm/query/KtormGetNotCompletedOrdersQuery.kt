package github.com.hukuta94.delivery.infrastructure.orm.ktorm.query

import github.com.hukuta94.delivery.core.application.query.order.GetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.core.application.query.order.GetNotCompletedOrdersResponse
import github.com.hukuta94.delivery.core.application.query.order.OrderResponse
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.infrastructure.orm.commons.toLocationResponse
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.notNull
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.OrderTable
import org.ktorm.database.Database
import org.ktorm.dsl.from
import org.ktorm.dsl.inList
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.where

class KtormGetNotCompletedOrdersQuery(
    private val database: Database,
) : GetNotCompletedOrdersQuery {

    override fun execute(): GetNotCompletedOrdersResponse {
        val orders = database.from(OrderTable)
            .select(
                OrderTable.id,
                OrderTable.location,
            )
            .where { OrderTable.statusId inList listOf(OrderStatus.CREATED.id, OrderStatus.ASSIGNED.id) }
            .map { row ->
                OrderResponse(
                    id = row.notNull(OrderTable.id),
                    location = row.notNull(OrderTable.location).toLocationResponse(),
                )
            }
        return GetNotCompletedOrdersResponse(orders)
    }
}
