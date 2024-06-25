package github.com.hukuta94.delivery.core.domain.courier

import github.com.hukuta94.delivery.core.domain.sharedkernel.Location
import github.com.hukuta94.delivery.core.domain.sharedkernel.randomLocation

fun newCourier(
    name: String? = null,
    transport: Transport? = null,
    location: Location? = null,
) = Courier.create(
    name = name ?: "Courier Name",
    transport = transport ?: Transport.PEDESTRIAN,
    location = location ?: randomLocation(),
)