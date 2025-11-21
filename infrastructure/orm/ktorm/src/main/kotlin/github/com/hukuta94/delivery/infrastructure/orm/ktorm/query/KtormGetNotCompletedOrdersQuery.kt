package github.com.hukuta94.delivery.infrastructure.orm.ktorm.query

import github.com.hukuta94.delivery.core.application.query.order.GetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.core.application.query.order.response.GetNotCompletedOrdersResponse
import github.com.hukuta94.delivery.core.application.query.order.response.OrderResponse
import github.com.hukuta94.delivery.infrastructure.orm.commons.toLocationResponse
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.notNull
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.CourierTable
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.OrderTable
import org.ktorm.database.Database
import org.ktorm.dsl.from
import org.ktorm.dsl.map
import org.ktorm.dsl.select

class KtormGetNotCompletedOrdersQuery(
    private val database: Database,
) : GetNotCompletedOrdersQuery {

    override fun execute(): GetNotCompletedOrdersResponse {
        val orders = database.from(OrderTable)
            .select(
                OrderTable.id,
                OrderTable.location,
            )
            .map { row ->
                OrderResponse(
                    id = row.notNull(CourierTable.id),
                    location = row.notNull(CourierTable.location).toLocationResponse(),
                )
            }
        return GetNotCompletedOrdersResponse(orders)
    }
}
