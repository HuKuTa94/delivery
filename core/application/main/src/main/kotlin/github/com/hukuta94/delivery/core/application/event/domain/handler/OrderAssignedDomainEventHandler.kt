package github.com.hukuta94.delivery.core.application.event.domain.handler

import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedDomainEvent
import github.com.hukuta94.delivery.core.application.port.BusProducerPort

class OrderAssignedDomainEventHandler(
    private val busProducerPort: BusProducerPort,
) : DomainEventHandler<OrderAssignedDomainEvent> {

    override val eventType = OrderAssignedDomainEvent::class

    override fun handle(event: OrderAssignedDomainEvent) {
        busProducerPort.publishOrderAssignedDomainEvent(event)
    }
}