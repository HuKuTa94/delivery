package github.com.hukuta94.delivery.core.domain.aggregate.courier

import github.com.hukuta94.delivery.core.domain.common.Location
import github.com.hukuta94.delivery.core.domain.common.randomLocation
import java.util.UUID

fun freeCourier(
    id: UUID? = null,
    name: CourierName? = null,
    location: Location? = null,
    transport: Transport? = null,
) = Courier.create(
    id = id ?: UUID.randomUUID(),
    name = name ?: courierName(),
    location = location ?: randomLocation(),
    transport = transport ?: Transport.PEDESTRIAN,
)

fun busyCourier(
    name: CourierName? = null,
    transport: Transport? = null,
    location: Location? = null,
    id: UUID? = null,
) = freeCourier(
    id = id,
    name = name,
    location = location,
    transport = transport
).apply {
    this.busy()
}