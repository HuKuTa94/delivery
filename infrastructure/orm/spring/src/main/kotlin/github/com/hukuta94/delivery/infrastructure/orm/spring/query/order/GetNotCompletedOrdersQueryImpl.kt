package github.com.hukuta94.delivery.infrastructure.orm.spring.query.order

import github.com.hukuta94.delivery.core.application.query.common.LocationResponse
import github.com.hukuta94.delivery.core.application.query.order.GetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.core.application.query.order.response.GetNotCompletedOrdersResponse
import github.com.hukuta94.delivery.core.application.query.order.response.OrderResponse
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.infrastructure.orm.commons.Orders.Column.ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Orders.Column.LOCATION
import github.com.hukuta94.delivery.infrastructure.orm.commons.Orders.Column.STATUS_ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Orders.TABLE_NAME
import github.com.hukuta94.delivery.infrastructure.orm.spring.model.converter.LocationConverter
import github.com.hukuta94.delivery.infrastructure.orm.spring.query.AbstractQuery
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet
import java.util.*

class GetNotCompletedOrdersQueryImpl(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val locationConverter: LocationConverter,
) : AbstractQuery<OrderResponse>(), GetNotCompletedOrdersQuery {

    override fun execute(): GetNotCompletedOrdersResponse {
        val orders = jdbcTemplate.query(SQL, PARAMETERS) { rs, _ -> mapRowToResponseDto(rs) }
        return GetNotCompletedOrdersResponse(
            orders = orders
        )
    }

    override fun mapRowToResponseDto(rs: ResultSet): OrderResponse {
        val location = locationConverter.convertToEntityAttribute(
            rs.getString(LOCATION)
        )
        return OrderResponse(
            id = rs.getObject(ID, UUID::class.java),
            location = LocationResponse(
                x = location.x,
                y = location.y
            )
        )
    }

    companion object {
        private val PARAMETERS = mapOf(
            "status1" to OrderStatus.CREATED.id,
            "status2" to OrderStatus.ASSIGNED.id,
        )
        private val SQL = """
            SELECT * 
            FROM $TABLE_NAME
            WHERE $STATUS_ID IN (:status1, :status2)
        """.trimIndent()
    }
}