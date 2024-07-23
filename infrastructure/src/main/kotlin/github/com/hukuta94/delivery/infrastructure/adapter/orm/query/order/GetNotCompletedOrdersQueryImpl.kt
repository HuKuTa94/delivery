package github.com.hukuta94.delivery.infrastructure.adapter.orm.query.order

import github.com.hukuta94.delivery.core.application.query.common.Location
import github.com.hukuta94.delivery.core.application.query.order.GetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.core.application.query.order.response.GetNotCompletedOrdersResponse
import github.com.hukuta94.delivery.core.application.query.order.response.Order
import github.com.hukuta94.delivery.core.domain.order.OrderStatus
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.LocationConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.OrderJpaEntity
import github.com.hukuta94.delivery.infrastructure.adapter.orm.query.AbstractQuery
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet
import java.util.*

class GetNotCompletedOrdersQueryImpl(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val locationConverter: LocationConverter,
) : AbstractQuery<Order>(), GetNotCompletedOrdersQuery {

    override fun execute(): GetNotCompletedOrdersResponse {
        val orders = jdbcTemplate.query(SQL, PARAMETERS) { rs, _ -> mapRowToResponseDto(rs) }
        return GetNotCompletedOrdersResponse(
            orders = orders
        )
    }

    override fun mapRowToResponseDto(rs: ResultSet): Order {
        val location = locationConverter.convertToEntityAttribute(
            rs.getString(OrderJpaEntity::location.name)
        )
        return Order(
            id = rs.getObject(OrderJpaEntity::id.name, UUID::class.java),
            location = Location(
                x = location.abscissa,
                y = location.ordinate
            )
        )
    }

    companion object {
        private val STATUS = OrderJpaEntity::status.name
        private val PARAMETERS = mapOf(
            "status1" to OrderStatus.CREATED.name,
            "status2" to OrderStatus.ASSIGNED.name,
        )
        private val SQL = """
            SELECT * FROM ${OrderJpaEntity.TABLE_NAME}
            WHERE $STATUS IN (:status1, :status2)
        """.trimIndent()
    }
}