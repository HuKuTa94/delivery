package github.com.hukuta94.delivery.infrastructure.persistence.inmemory

import github.com.hukuta94.delivery.core.domain.aggregate.order.Order
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryPort
import java.util.*

class InMemoryOrderRepository : OrderRepositoryPort {

    private val storage = mutableMapOf<UUID, Order>()

    override fun add(aggregate: Order) {
        storage[aggregate.id] = aggregate
    }

    override fun update(aggregate: Order) {
        storage[aggregate.id] = aggregate
    }

    override fun update(aggregates: Collection<Order>) {
        aggregates.forEach { aggregate ->
            update(aggregate)
        }
    }

    override fun getById(id: UUID): Order {
        return storage[id] ?: error("The order with id=$id is not found")
    }

    override fun existsById(id: UUID): Boolean {
        return storage.containsKey(id)
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
