package github.com.hukuta94.delivery.core.domain.aggregate.courier

import github.com.hukuta94.delivery.core.domain.common.Location
import github.com.hukuta94.delivery.core.domain.common.newLocationWithRandomCoords
import java.util.UUID

fun newCourier(
    name: CourierName? = null,
    transport: Transport? = null,
    location: Location? = null,
    id: UUID? = null,
) = Courier.create(
    name = name ?: courierName(),
    transport = transport ?: pedestrian(),
    location = location ?: newLocationWithRandomCoords(),
    id = id ?: UUID.randomUUID(),
)

fun newBusyCourier(
    name: CourierName? = null,
    transport: Transport? = null,
    location: Location? = null,
    id: UUID? = null,
) = newCourier(name, transport, location, id).apply {
    this.busy()
}

fun newFreeCourier(
    name: CourierName? = null,
    transport: Transport? = null,
    location: Location? = null,
    id: UUID? = null,
) = newCourier(name, transport, location, id).apply {
    this.free()
}