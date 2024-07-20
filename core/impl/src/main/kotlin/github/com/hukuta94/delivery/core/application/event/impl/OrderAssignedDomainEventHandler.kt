package github.com.hukuta94.delivery.core.application.event.impl

import github.com.hukuta94.delivery.core.application.event.DomainEventListener
import github.com.hukuta94.delivery.core.domain.order.OrderAssignedDomainEvent
import github.com.hukuta94.delivery.core.port.BusProducer

class OrderAssignedDomainEventHandler(
    private val busProducer: BusProducer,
) : DomainEventListener<OrderAssignedDomainEvent> {

    override val eventType = OrderAssignedDomainEvent::class

    override fun handle(domainEvent: OrderAssignedDomainEvent) {
        busProducer.publishOrderAssignedDomainEvent(domainEvent)
    }
}