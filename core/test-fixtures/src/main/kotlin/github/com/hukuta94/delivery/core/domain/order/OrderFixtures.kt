package github.com.hukuta94.delivery.core.domain.order

import github.com.hukuta94.delivery.core.domain.courier.newCourier
import github.com.hukuta94.delivery.core.domain.sharedkernel.Location
import java.util.*

fun newOrder() = Order.create(
    id = UUID.randomUUID(),
    location = Location.random(),
)

fun assignedOrder() = newOrder().apply {
    assignCourier(newCourier())
}

fun completedOrder() = assignedOrder().apply {
    complete()
}
