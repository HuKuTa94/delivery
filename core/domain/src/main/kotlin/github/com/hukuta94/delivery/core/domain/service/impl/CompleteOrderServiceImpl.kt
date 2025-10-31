package github.com.hukuta94.delivery.core.domain.service.impl

import arrow.core.raise.either
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.order.Order
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.service.CompleteOrderService

class CompleteOrderServiceImpl : CompleteOrderService {

    override fun execute(order: Order, courier: Courier) = either {
        if (order.status != OrderStatus.ASSIGNED) {
            raise(CompleteOrderService.Error.OrderIsNotAssigned)
        }
        if (order.courierId != courier.id) {
            raise(CompleteOrderService.Error.OrderAssignedToAnotherCourier)
        }
        if (courier.location != order.location) {
            raise(CompleteOrderService.Error.CourierNotReachedOrderLocation)
        }

        courier.free()
        order.complete()
    }
}