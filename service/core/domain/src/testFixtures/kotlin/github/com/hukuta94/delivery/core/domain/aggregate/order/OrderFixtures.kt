package github.com.hukuta94.delivery.core.domain.aggregate.order

import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.common.Location
import github.com.hukuta94.delivery.core.domain.common.randomLocation
import java.util.*

fun createdOrder(
    id: UUID? = null,
    location: Location? = null,
) = Order.create(
    id = id ?: UUID.randomUUID(),
    location = location ?: randomLocation(),
)

fun assignedOrder(
    id: UUID? = null,
    location: Location? = null,
    courier: Courier? = null,
) = createdOrder(
    id = id,
    location = location
).apply {
    assignCourier(
        courier ?: freeCourier()
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
