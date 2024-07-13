package github.com.hukuta94.delivery.core.port

import github.com.hukuta94.delivery.core.domain.order.OrderAssignedDomainEvent
import github.com.hukuta94.delivery.core.domain.order.OrderCompletedDomainEvent

interface BusProducer {
    fun publishOrderAssignedDomainEvent(orderAssignedDomainEvent: OrderAssignedDomainEvent)

    fun publishOrderCompletedDomainEvent(orderCompletedDomainEvent: OrderCompletedDomainEvent)
}