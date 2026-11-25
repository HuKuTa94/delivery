package github.com.hukuta94.delivery.core.domain.rule.impl

import arrow.core.raise.either
import arrow.core.raise.ensure
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.order.Order
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.rule.CompleteOrderBusinessRule

class CompleteOrderBusinessRuleImpl : CompleteOrderBusinessRule {

    override fun execute(
        order: Order,
        courier: Courier,
    ) = either {
        ensure(order.status == OrderStatus.ASSIGNED) {
            CompleteOrderBusinessRule.Error.OrderIsNotAssigned
        }
        ensure(order.courierId == courier.id) {
            CompleteOrderBusinessRule.Error.OrderAssignedToAnotherCourier
        }
        ensure(courier.location == order.location) {
            CompleteOrderBusinessRule.Error.CourierNotReachedOrderLocation
        }

        courier.free()
        order.complete()
    }
}
