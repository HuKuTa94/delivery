package github.com.hukuta94.delivery.core.application.usecase.order.query

import java.util.*

interface GetNotCompletedOrdersQuery {
    fun execute(): List<Response>
}

data class Response(
    val orderId: UUID,
    val locationAbscissa: Int,
    val locationOrdinate: Int,
)