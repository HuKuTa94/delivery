package github.com.hukuta94.delivery.infrastructure.orm.springjpa.query.courier

import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.application.query.courier.response.CourierResponse
import github.com.hukuta94.delivery.core.application.query.courier.response.GetCouriersResponse
import github.com.hukuta94.delivery.core.application.query.common.LocationResponse
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.Column.ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.Column.LOCATION
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.Column.NAME
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.Column.STATUS_ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.Column.TRANSPORT_ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.TABLE_NAME
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.converter.LocationConverter
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.query.AbstractQuery
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet
import java.util.*

class GetBusyCouriersQueryImpl(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val locationConverter: LocationConverter,
) : AbstractQuery<CourierResponse>(), GetBusyCouriersQuery {

    override fun execute(): GetCouriersResponse {
        val couriers = jdbcTemplate.query(SQL, PARAMETERS) { rs, _ -> mapRowToResponseDto(rs) }
        return GetCouriersResponse(
            couriers = couriers
        )
    }

    override fun mapRowToResponseDto(rs: ResultSet): CourierResponse {
        val location = locationConverter.convertToEntityAttribute(
            rs.getString(LOCATION)
        )
        return CourierResponse(
            id = rs.getObject(ID, UUID::class.java),
            name = rs.getString(NAME),
            location = LocationResponse(
                x = location.x,
                y = location.y
            ),
            transportId = rs.getInt(TRANSPORT_ID)
        )
    }

    companion object {
        private val PARAMETERS = mapOf(
            STATUS_ID to CourierStatus.BUSY.id,
        )

        private val SQL = """
            SELECT * 
            FROM $TABLE_NAME
            WHERE $STATUS_ID = :$STATUS_ID
        """.trimIndent()
    }
}