package github.com.hukuta94.delivery.infrastructure.orm.ktorm.query

import github.com.hukuta94.delivery.core.application.query.common.LocationResponse
import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.application.query.courier.response.CourierResponse
import github.com.hukuta94.delivery.core.application.query.courier.response.GetCouriersResponse
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.CourierTable
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.CourierTransportTable
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.leftJoin
import org.ktorm.dsl.map
import org.ktorm.dsl.select

class KtormGetBusyCouriersQuery(
    private val database: Database,
) : GetBusyCouriersQuery {

    override fun execute(): GetCouriersResponse {
        val couriers = database.from(CourierTable)
            .leftJoin(CourierTransportTable, on = CourierTable.transportId eq CourierTransportTable.id)
            .select(
                CourierTable.id,
                CourierTable.name,
                CourierTable.location,
                CourierTable.transportId,
            )
            .map { row ->
                val location = row[CourierTable.location]!!.split(",")
                //TODO вынести общую логику в модуль orm:commons
                CourierResponse(
                    id = row[CourierTable.id]!!,
                    name = row[CourierTable.name]!!,
                    location = LocationResponse(
                        x = location[0].toInt(),
                        y = location[1].toInt(),
                    ),
                    transportId = row[CourierTable.transportId]!!,
                )
            }
        return GetCouriersResponse(couriers)
    }
}