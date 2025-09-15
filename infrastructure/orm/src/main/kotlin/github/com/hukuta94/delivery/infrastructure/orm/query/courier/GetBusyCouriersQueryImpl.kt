package github.com.hukuta94.delivery.infrastructure.orm.query.courier

import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.application.query.courier.response.CourierResponse
import github.com.hukuta94.delivery.core.application.query.courier.response.GetCouriersResponse
import github.com.hukuta94.delivery.core.application.query.common.LocationResponse
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.infrastructure.orm.model.converter.LocationConverter
import github.com.hukuta94.delivery.infrastructure.orm.model.entity.CourierJpaEntity
import github.com.hukuta94.delivery.infrastructure.orm.query.AbstractQuery
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
            rs.getString(CourierJpaEntity::location.name)
        )
        return CourierResponse(
            id = rs.getObject(CourierJpaEntity::id.name, UUID::class.java),
            name = rs.getString(CourierJpaEntity::name.name),
            location = LocationResponse(
                x = location.x,
                y = location.y
            ),
            transportId = rs.getInt(CourierJpaEntity.TRANSPORT_ID)
        )
    }

    companion object {
        private val PARAMETERS = mapOf(
            CourierJpaEntity.STATUS_ID to CourierStatus.BUSY.id,
        )

        private val SQL = """
            SELECT * 
            FROM ${CourierJpaEntity.TABLE_NAME}
            WHERE ${CourierJpaEntity.STATUS_ID} = :${CourierJpaEntity.STATUS_ID}
        """.trimIndent()
    }
}