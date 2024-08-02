package github.com.hukuta94.delivery.core.application.event.domain.handler

import github.com.hukuta94.delivery.core.domain.order.OrderAssignedDomainEvent
import github.com.hukuta94.delivery.core.application.port.BusProducer

class OrderAssignedDomainEventHandler(
    private val busProducer: BusProducer,
) : DomainEventHandler<OrderAssignedDomainEvent> {

    override val eventType = OrderAssignedDomainEvent::class

    override fun handle(event: OrderAssignedDomainEvent) {
        busProducer.publishOrderAssignedDomainEvent(event)
    }
}