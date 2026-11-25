package github.com.hukuta94.delivery.core.application.query.courier

import github.com.hukuta94.delivery.core.application.query.Query
import github.com.hukuta94.delivery.core.application.query.common.LocationResponse
import java.util.UUID

interface GetBusyCouriersQuery : Query {
    fun execute(): GetCouriersResponse
}

data class GetCouriersResponse(
    val couriers: List<CourierResponse>,
)

data class CourierResponse(
    val id: UUID,
    val name: String,
    val location: LocationResponse,
    val transportId: Int,
)
