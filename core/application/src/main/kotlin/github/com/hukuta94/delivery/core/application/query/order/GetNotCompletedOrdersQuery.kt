package github.com.hukuta94.delivery.core.application.query.order

import github.com.hukuta94.delivery.core.application.query.Query
import github.com.hukuta94.delivery.core.application.query.common.LocationResponse
import java.util.UUID

interface GetNotCompletedOrdersQuery : Query {
    fun execute(): GetNotCompletedOrdersResponse
}

data class GetNotCompletedOrdersResponse(
    val orders: List<OrderResponse>,
)

data class OrderResponse(
    val id: UUID,
    val location: LocationResponse,
)