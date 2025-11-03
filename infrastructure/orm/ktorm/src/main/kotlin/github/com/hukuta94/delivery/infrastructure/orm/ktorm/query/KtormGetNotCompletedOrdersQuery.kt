package github.com.hukuta94.delivery.infrastructure.orm.ktorm.query

import github.com.hukuta94.delivery.core.application.query.common.LocationResponse
import github.com.hukuta94.delivery.core.application.query.order.GetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.core.application.query.order.response.GetNotCompletedOrdersResponse
import github.com.hukuta94.delivery.core.application.query.order.response.OrderResponse
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
                val location = row[CourierTable.location]!!.split(",")
                OrderResponse(
                    id = row[CourierTable.id]!!,
                    //TODO вынести общую логику в модуль orm:commons
                    location = LocationResponse(
                        x = location[0].toInt(),
                        y = location[1].toInt(),
                    ),
                )
            }
        return GetNotCompletedOrdersResponse(orders)
    }
}