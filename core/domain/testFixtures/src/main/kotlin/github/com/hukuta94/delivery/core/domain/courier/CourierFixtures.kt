package github.com.hukuta94.delivery.core.domain.courier

import github.com.hukuta94.delivery.core.domain.common.Location
import github.com.hukuta94.delivery.core.domain.common.randomLocation

fun newCourier(
    name: CourierName? = null,
    transport: Transport? = null,
    location: Location? = null,
) = Courier.create(
    name = name ?: courierName(),
    transport = transport ?: Transport.PEDESTRIAN,
    location = location ?: randomLocation(),
)

fun busyCourier(
    name: CourierName? = null,
    transport: Transport? = null,
    location: Location? = null,
) = newCourier(name, transport, location).apply {
    this.busy()
}

fun freeCourier(
    name: CourierName? = null,
    transport: Transport? = null,
    location: Location? = null,
) = newCourier(name, transport, location).apply {
    this.free()
}