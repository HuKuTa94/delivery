package github.com.hukuta94.delivery.core.application.port.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.order.Order
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import java.util.UUID

class OrderRepositoryFake : AggregateRepositoryFake<Order, UUID>(), OrderRepositoryPort {

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