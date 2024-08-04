package github.com.hukuta94.delivery.core.application.query.order.response

import github.com.hukuta94.delivery.core.application.query.common.LocationResponse
import java.util.*

data class OrderResponse(
    val id: UUID,
    val location: LocationResponse,
)