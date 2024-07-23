package github.com.hukuta94.delivery.infrastructure.adapter.orm.query.courier

import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.application.query.courier.response.Courier
import github.com.hukuta94.delivery.core.application.query.courier.response.GetCouriersResponse
import github.com.hukuta94.delivery.core.application.query.common.Location
import github.com.hukuta94.delivery.core.domain.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.courier.Transport
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.LocationConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.CourierJpaEntity
import github.com.hukuta94.delivery.infrastructure.adapter.orm.query.AbstractQuery
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet
import java.util.*

class GetBusyCouriersQueryImpl(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val locationConverter: LocationConverter,
) : AbstractQuery<Courier>(), GetBusyCouriersQuery {

    override fun execute(): GetCouriersResponse {
        val couriers = jdbcTemplate.query(SQL, PARAMETERS) { rs, _ -> mapRowToResponseDto(rs) }
        return GetCouriersResponse(
            couriers = couriers
        )
    }

    override fun mapRowToResponseDto(rs: ResultSet): Courier {
        val location = locationConverter.convertToEntityAttribute(
            rs.getString(CourierJpaEntity::location.name)
        )
        return Courier(
            id = rs.getObject(CourierJpaEntity::id.name, UUID::class.java),
            name = rs.getString(CourierJpaEntity::name.name),
            location = Location(
                x = location.abscissa,
                y = location.ordinate
            ),
            transport = Transport.valueOf(
                rs.getString(CourierJpaEntity::transport.name)
            )
        )
    }

    companion object {
        private val STATUS = CourierJpaEntity::status.name
        private val PARAMETERS = mapOf(
            STATUS to CourierStatus.FREE.name,
        )
        private val SQL = "SELECT * FROM ${CourierJpaEntity.TABLE_NAME} WHERE $STATUS = :$STATUS"
    }
}