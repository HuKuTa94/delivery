package github.com.hukuta94.delivery.core.application.query.order.response

import github.com.hukuta94.delivery.core.application.query.common.Location
import java.util.*

data class Order(
    val id: UUID,
    val location: Location,
)