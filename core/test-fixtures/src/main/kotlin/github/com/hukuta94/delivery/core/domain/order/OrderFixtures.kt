package github.com.hukuta94.delivery.core.domain.order

import github.com.hukuta94.delivery.core.domain.courier.Courier
import github.com.hukuta94.delivery.core.domain.courier.newCourier
import github.com.hukuta94.delivery.core.domain.common.Location
import java.util.*

fun newOrder(
    id: UUID? = null,
    location: Location? = null,
) = Order.create(
    id = id ?: UUID.randomUUID(),
    location = location ?: Location.random(),
)

fun assignedOrder(
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

fun completedOrder(
    id: UUID? = null,
    location: Location? = null,
    courier: Courier? = null,
) = assignedOrder(
    id = id,
    location = location,
    courier = courier
).apply {
    complete()
}
