package github.com.hukuta94.delivery.infrastructure.orm.ktorm.query

import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.application.query.courier.response.CourierResponse
import github.com.hukuta94.delivery.core.application.query.courier.response.GetCouriersResponse
import github.com.hukuta94.delivery.infrastructure.orm.commons.toLocationResponse
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.notNull
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.CourierTable
import org.ktorm.database.Database
import org.ktorm.dsl.from
import org.ktorm.dsl.map
import org.ktorm.dsl.select

class KtormGetBusyCouriersQuery(
    private val database: Database,
) : GetBusyCouriersQuery {

    override fun execute(): GetCouriersResponse {
        val couriers = database.from(CourierTable)
            .select(
                CourierTable.id,
                CourierTable.name,
                CourierTable.location,
                CourierTable.transportId,
            )
            .map { row ->
                CourierResponse(
                    id = row.notNull(CourierTable.id),
                    name = row.notNull(CourierTable.name),
                    location = row.notNull(CourierTable.location).toLocationResponse(),
                    transportId = row.notNull(CourierTable.transportId),
                )
            }
        return GetCouriersResponse(couriers)
    }
}