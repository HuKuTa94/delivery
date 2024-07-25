package github.com.hukuta94.delivery.infrastructure.adapter.inmemory

import github.com.hukuta94.delivery.core.application.event.DomainEventPublisher
import github.com.hukuta94.delivery.core.domain.order.Order
import github.com.hukuta94.delivery.core.domain.order.OrderStatus
import github.com.hukuta94.delivery.core.port.OrderRepository
import java.util.UUID

class OrderInMemoryRepository(
    domainEventPublisher: DomainEventPublisher
) : OrderRepository(domainEventPublisher) {

    private val storage = mutableMapOf<UUID, Order>()

    override fun add(aggregate: Order) {
        publishDomainEvents(aggregate)
        storage[aggregate.id] = aggregate
    }

    override fun update(aggregate: Order) {
        publishDomainEvents(aggregate)
        storage[aggregate.id] = aggregate
    }

    override fun update(aggregates: Collection<Order>) {
        aggregates.forEach { aggregate ->
            update(aggregate)
        }
    }

    override fun getById(id: UUID): Order {
        return storage[id] ?:  error("The order with id=$id is not found")
    }

    override fun getAllCreated(): Collection<Order> {
        return storage.values.filter { order ->
            order.status == OrderStatus.CREATED
        }
    }

    override fun getAllAssigned(): Collection<Order> {
        return storage.values.filter { order ->
            order.status == OrderStatus.ASSIGNED
        }
    }

    override fun getAllNotCompleted(): Collection<Order> {
        return storage.values.filter { order ->
            order.status != OrderStatus.COMPLETED
        }
    }
}