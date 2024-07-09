package github.com.hukuta94.delivery.api.adapter.http.order

import github.com.hukuta94.delivery.api.adapter.http.common.Location
import java.util.*

data class Order(
    val id: UUID,
    val courierLocation: Location
)