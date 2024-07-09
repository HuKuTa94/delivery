package github.com.hukuta94.delivery.core.domain.service.impl

import github.com.hukuta94.delivery.core.domain.courier.Courier
import github.com.hukuta94.delivery.core.domain.order.Order
import github.com.hukuta94.delivery.core.domain.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.service.CompleteOrderService

class CompleteOrderServiceImpl : CompleteOrderService {

    override fun execute(order: Order, courier: Courier): Boolean {
        if (canNotCompleteOrder(order, courier)) {
            return false
        }

        completeOrder(order, courier)

        return true
    }

    private fun canNotCompleteOrder(order: Order, courier: Courier): Boolean {
        val isDifferentCourier = order.courierId != courier.id
        val isNotAssignedOrder = order.status != OrderStatus.ASSIGNED
        val courierNotReachedOrderLocation = courier.location != order.location

        return isDifferentCourier || isNotAssignedOrder || courierNotReachedOrderLocation
    }

    private fun completeOrder(order: Order, courier: Courier) {
        courier.free()
        order.complete()
    }
}