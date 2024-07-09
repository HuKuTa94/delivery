package github.com.hukuta94.delivery.infrastructure.adapter.inmemory

import github.com.hukuta94.delivery.core.domain.order.Order
import github.com.hukuta94.delivery.core.domain.order.OrderStatus
import github.com.hukuta94.delivery.core.port.OrderRepository
import java.util.UUID

class OrderInMemoryRepository : OrderRepository {

    private val storage = mutableMapOf<UUID, Order>()

    override fun add(order: Order) {
        storage[order.id] = order
    }

    override fun update(order: Order) {
        storage[order.id] = order
    }

    override fun update(orders: List<Order>) {
        orders.forEach { order ->
            storage[order.id] = order
        }
    }

    override fun getById(id: UUID): Order {
        return storage[id] ?:  error("The order with id=$id is not found")
    }

    override fun getAllCreated(): List<Order> {
        return storage.values.filter { order ->
            order.status == OrderStatus.CREATED
        }
    }

    override fun getAllAssigned(): List<Order> {
        return storage.values.filter { order ->
            order.status == OrderStatus.ASSIGNED
        }
    }

    override fun getAllNotCompleted(): List<Order> {
        return storage.values.filter { order ->
            order.status != OrderStatus.COMPLETED
        }
    }
}