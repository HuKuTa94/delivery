package github.com.hukuta94.delivery.core.domain.order

import github.com.hukuta94.delivery.core.domain.courier.newCourier
import github.com.hukuta94.delivery.core.domain.sharedkernel.Location
import java.util.*

fun newOrder(
    id: UUID? = null,
    location: Location? = null,
) = Order.create(
    id = UUID.randomUUID(),
    location = Location.random(),
)

fun assignedOrder(
    id: UUID? = null,
    location: Location? = null
) = newOrder(id, location).apply {
    assignCourier(newCourier())
}

fun completedOrder(
    id: UUID? = null,
    location: Location? = null
) = assignedOrder(id, location).apply {
    complete()
}
