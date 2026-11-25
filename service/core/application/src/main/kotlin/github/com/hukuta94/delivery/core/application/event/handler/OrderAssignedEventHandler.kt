package github.com.hukuta94.delivery.core.application.event.handler

import github.com.hukuta94.delivery.core.application.event.EventHandler
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedEvent
import github.com.hukuta94.delivery.core.application.port.BusProducerPort

class OrderAssignedEventHandler(
    private val busProducerPort: BusProducerPort,
) : EventHandler<OrderAssignedEvent> {

    override val eventType = OrderAssignedEvent::class

    override fun handle(event: OrderAssignedEvent) {
        busProducerPort.publishOrderAssignedDomainEvent(event)
    }
}
