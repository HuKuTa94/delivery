package github.com.hukuta94.delivery.core.application.event.handler

import github.com.hukuta94.delivery.core.application.event.ApplicationEventHandler
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedDomainEvent
import github.com.hukuta94.delivery.core.application.port.BusProducerPort

class OrderCompletedDomainEventHandler(
    private val busProducerPort: BusProducerPort,
) : ApplicationEventHandler<OrderCompletedDomainEvent> {

    override val eventType = OrderCompletedDomainEvent::class

    override fun handle(event: OrderCompletedDomainEvent) {
        busProducerPort.publishOrderCompletedDomainEvent(event)
    }
}