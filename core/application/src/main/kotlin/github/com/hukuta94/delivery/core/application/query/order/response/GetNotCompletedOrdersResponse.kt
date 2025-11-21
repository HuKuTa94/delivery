package github.com.hukuta94.delivery.core.application.query.order.response

data class GetNotCompletedOrdersResponse(
    val orders: List<OrderResponse>,
) {
    companion object {
        fun empty() = GetNotCompletedOrdersResponse(
            orders = emptyList(),
        )
    }
}
