package github.com.hukuta94.delivery.core.domain.order

import github.com.hukuta94.delivery.core.domain.sharedkernel.Location
import java.util.*

fun newOrder() = Order.create(
    id = UUID.randomUUID(),
    location = Location.random(),
)

fun completedOrder() = newOrder().apply {
    status = OrderStatus.COMPLETED
}

fun assignedOrder() = newOrder().apply {
    status = OrderStatus.ASSIGNED
    courierId = UUID.randomUUID()
}