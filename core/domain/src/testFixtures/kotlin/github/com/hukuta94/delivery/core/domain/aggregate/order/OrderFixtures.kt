package github.com.hukuta94.delivery.core.domain.aggregate.order

import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.newCourier
import github.com.hukuta94.delivery.core.domain.common.Location
import github.com.hukuta94.delivery.core.domain.common.newLocationWithRandomCoords
import java.util.*

fun newOrder(
    id: UUID? = null,
    location: Location? = null,
) = Order.create(
    id = id ?: UUID.randomUUID(),
    location = location ?: newLocationWithRandomCoords(),
)

fun newAssignedOrder(
    id: UUID? = null,
    location: Location? = null,
    courier: Courier? = null,
) = newOrder(
    id = id,
    location = location
).apply {
    assignCourier(
        courier ?: newCourier()
    )
}

fun newCompletedOrder(
    id: UUID? = null,
    location: Location? = null,
    courier: Courier? = null,
) = newAssignedOrder(
    id = id,
    location = location,
    courier = courier
).apply {
    complete()
}
