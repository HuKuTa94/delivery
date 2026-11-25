package github.com.hukuta94.delivery.core.domain.rule

import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.order.Order

interface DispatchOrderToCourierBusinessRule : BusinessRule {

    fun execute(
        order: Order,
        couriers: Collection<Courier>,
    )
}
