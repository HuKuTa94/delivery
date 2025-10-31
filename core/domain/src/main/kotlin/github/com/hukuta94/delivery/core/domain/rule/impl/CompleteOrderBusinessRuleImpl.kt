package github.com.hukuta94.delivery.core.domain.rule.impl

import arrow.core.raise.either
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.order.Order
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.rule.CompleteOrderBusinessRule

class CompleteOrderBusinessRuleImpl : CompleteOrderBusinessRule {

    override fun execute(order: Order, courier: Courier) = either {
        if (order.status != OrderStatus.ASSIGNED) {
            raise(CompleteOrderBusinessRule.Error.OrderIsNotAssigned)
        }
        if (order.courierId != courier.id) {
            raise(CompleteOrderBusinessRule.Error.OrderAssignedToAnotherCourier)
        }
        if (courier.location != order.location) {
            raise(CompleteOrderBusinessRule.Error.CourierNotReachedOrderLocation)
        }

        courier.free()
        order.complete()
    }
}