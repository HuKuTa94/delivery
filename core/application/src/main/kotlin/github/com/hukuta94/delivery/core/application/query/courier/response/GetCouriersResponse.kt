package github.com.hukuta94.delivery.core.application.query.courier.response

data class GetCouriersResponse(
    val couriers: List<CourierResponse>,
) {
    companion object {
        fun empty() = GetCouriersResponse(
            couriers = emptyList(),
        )
    }
}
