package github.com.hukuta94.delivery.core.port

import github.com.hukuta94.delivery.core.domain.order.Order

abstract class OrderRepository() : Repository<Order>() {

    abstract fun getAllCreated(): Collection<Order>

    abstract fun getAllAssigned(): Collection<Order>

    abstract fun getAllNotCompleted(): Collection<Order>
}