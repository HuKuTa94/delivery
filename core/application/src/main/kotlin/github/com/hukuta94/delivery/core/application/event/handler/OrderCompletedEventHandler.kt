package github.com.hukuta94.delivery.core.application.event.handler

import github.com.hukuta94.delivery.core.application.event.EventHandler
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedEvent
import github.com.hukuta94.delivery.core.application.port.BusProducerPort

class OrderCompletedEventHandler(
    private val busProducerPort: BusProducerPort,
) : EventHandler<OrderCompletedEvent> {

    override val eventType = OrderCompletedEvent::class

    override fun handle(event: OrderCompletedEvent) {
        busProducerPort.publishOrderCompletedDomainEvent(event)
    }
}
