package github.com.hukuta94.delivery.core.port

import github.com.hukuta94.delivery.core.application.event.DomainEventPublisher
import github.com.hukuta94.delivery.core.domain.order.Order

abstract class OrderRepository(
    domainEventPublisher: DomainEventPublisher,
) : Repository<Order>(domainEventPublisher) {

    abstract fun getAllCreated(): Collection<Order>

    abstract fun getAllAssigned(): Collection<Order>

    abstract fun getAllNotCompleted(): Collection<Order>
}