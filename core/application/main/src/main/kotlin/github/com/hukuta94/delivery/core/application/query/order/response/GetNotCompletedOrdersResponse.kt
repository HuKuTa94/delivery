package github.com.hukuta94.delivery.core.application.query.order.response

data class GetNotCompletedOrdersResponse(
    val orders: List<Order>,
) {
    companion object {
        fun empty() = GetNotCompletedOrdersResponse(
            orders = emptyList(),
        )
    }
}