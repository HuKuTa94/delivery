package github.com.hukuta94.delivery.core.domain.aggregate.order

import github.com.hukuta94.delivery.core.domain.aggregate.Aggregate
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.common.Location
import java.util.UUID

class Order internal constructor(
    override val id: UUID,
    val location: Location,
) : Aggregate<UUID>() {

    lateinit var status: OrderStatus
        private set

    var courierId: UUID? = null
        private set

    fun assignCourier(courier: Courier) {
        if (status != OrderStatus.CREATED) {
            return
        }

        status = OrderStatus.ASSIGNED
        courierId = courier.id
        raiseDomainEvent(OrderAssignedDomainEvent(id, courier.id))
    }

    fun complete() {
        if (courierId == null) {
            return
        }

        status = OrderStatus.COMPLETED
        raiseDomainEvent(OrderCompletedDomainEvent(id))
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