package github.com.hukuta94.delivery.core.application.query.courier.response

import github.com.hukuta94.delivery.core.application.query.common.LocationResponse
import java.util.*

data class CourierResponse(
    val id: UUID,
    val name: String,
    val location: LocationResponse,
    val transportId: Int,
)