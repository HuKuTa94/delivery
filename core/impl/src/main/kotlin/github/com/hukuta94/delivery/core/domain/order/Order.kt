package github.com.hukuta94.delivery.core.domain.order

import github.com.hukuta94.delivery.core.domain.courier.Courier
import github.com.hukuta94.delivery.core.domain.sharedkernel.Location
import java.util.UUID

class Order internal constructor(
    val id: UUID,
    val location: Location,
) {
    lateinit var status: OrderStatus
        private set

    var courierId: UUID? = null
        private set

    fun assignCourier(courier: Courier) {
        if (status != OrderStatus.CREATED) {
            return
        }

        this.status = OrderStatus.ASSIGNED
        this.courierId = courier.id
    }

    fun complete() {
        if (courierId == null) {
            return
        }

        status = OrderStatus.COMPLETED
    }

    companion object {
        fun create(id: UUID, location: Location): Order {
            return Order(
                id = id,
                location = location,
            ).apply {
                status = OrderStatus.CREATED
            }
        }
    }
}