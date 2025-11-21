package github.com.hukuta94.delivery.core.application.event.handler

import github.com.hukuta94.delivery.core.application.event.ApplicationEventHandler
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedDomainEvent
import github.com.hukuta94.delivery.core.application.port.BusProducerPort

class OrderAssignedDomainEventHandler(
    private val busProducerPort: BusProducerPort,
) : ApplicationEventHandler<OrderAssignedDomainEvent> {

    override val eventType = OrderAssignedDomainEvent::class

    override fun handle(event: OrderAssignedDomainEvent) {
        busProducerPort.publishOrderAssignedDomainEvent(event)
    }
}
