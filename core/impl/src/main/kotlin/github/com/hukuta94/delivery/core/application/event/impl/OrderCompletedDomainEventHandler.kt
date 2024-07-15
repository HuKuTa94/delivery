package github.com.hukuta94.delivery.core.application.event.impl

import github.com.hukuta94.delivery.core.application.event.DomainEventListener
import github.com.hukuta94.delivery.core.domain.order.OrderCompletedDomainEvent
import github.com.hukuta94.delivery.core.port.BusProducer

class OrderCompletedDomainEventHandler(
    private val busProducer: BusProducer,
) : DomainEventListener<OrderCompletedDomainEvent> {

    override val eventType = OrderCompletedDomainEvent::class

    override fun handle(domainEvent: OrderCompletedDomainEvent) {
        busProducer.publishOrderCompletedDomainEvent(domainEvent)
    }
}