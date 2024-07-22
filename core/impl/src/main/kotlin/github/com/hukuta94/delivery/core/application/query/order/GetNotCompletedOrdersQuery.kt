package github.com.hukuta94.delivery.core.application.query.order

import java.util.*

interface GetNotCompletedOrdersQuery {
    fun execute(): List<GetNotCompletedOrderResponse>
}

data class GetNotCompletedOrderResponse(
    val orderId: UUID,
    val locationAbscissa: Int,
    val locationOrdinate: Int,
)