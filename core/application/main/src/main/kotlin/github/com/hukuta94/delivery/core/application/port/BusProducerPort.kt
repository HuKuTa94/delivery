package github.com.hukuta94.delivery.core.application.port

import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedDomainEvent
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedDomainEvent

interface BusProducerPort {
    fun publishOrderAssignedDomainEvent(orderAssignedDomainEvent: OrderAssignedDomainEvent)

    fun publishOrderCompletedDomainEvent(orderCompletedDomainEvent: OrderCompletedDomainEvent)
}