package github.com.hukuta94.delivery.core.domain.service.impl

import github.com.hukuta94.delivery.core.domain.service.DispatchService
import github.com.hukuta94.delivery.core.domain.courier.Courier
import github.com.hukuta94.delivery.core.domain.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.order.Order

class DispatchServiceImpl : DispatchService {

    override fun assignOrderToMostSuitableCourier(order: Order, couriers: List<Courier>) {
        couriers
            .filter { it.status == CourierStatus.FREE }
            .minByOrNull { it.timeToLocation(order.location) }
            ?.apply {
                this.busy()
                order.assignCourier(this)
            }
    }
}