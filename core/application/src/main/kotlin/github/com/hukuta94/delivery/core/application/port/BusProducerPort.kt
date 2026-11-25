package github.com.hukuta94.delivery.core.application.port

import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedEvent
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedEvent

interface BusProducerPort {
    fun publishOrderAssignedDomainEvent(orderAssignedEvent: OrderAssignedEvent)

    fun publishOrderCompletedDomainEvent(orderCompletedEvent: OrderCompletedEvent)
}
