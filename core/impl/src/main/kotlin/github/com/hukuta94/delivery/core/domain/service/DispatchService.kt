package github.com.hukuta94.delivery.core.domain.service

import github.com.hukuta94.delivery.core.domain.courier.Courier
import github.com.hukuta94.delivery.core.domain.order.Order

interface DispatchService {

    fun assignOrderToMostSuitableCourier(order: Order, couriers: List<Courier>)
}