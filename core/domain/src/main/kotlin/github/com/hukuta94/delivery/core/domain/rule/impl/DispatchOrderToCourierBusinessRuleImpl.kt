package github.com.hukuta94.delivery.core.domain.rule.impl

import github.com.hukuta94.delivery.core.domain.rule.DispatchOrderToCourierBusinessRule
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.Order

class DispatchOrderToCourierBusinessRuleImpl : DispatchOrderToCourierBusinessRule {

    override fun execute(order: Order, couriers: Collection<Courier>) {
        couriers
            .filter { it.status == CourierStatus.FREE }
            .minByOrNull { it.timeToLocation(order.location) }
            ?.apply {
                this.busy()
                order.assignCourier(this)
            }
    }
}