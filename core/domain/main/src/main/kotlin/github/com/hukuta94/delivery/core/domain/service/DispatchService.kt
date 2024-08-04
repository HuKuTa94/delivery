package github.com.hukuta94.delivery.core.domain.service

import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.order.Order

interface DispatchService {

    fun assignOrderToMostSuitableCourier(order: Order, couriers: Collection<Courier>)
}