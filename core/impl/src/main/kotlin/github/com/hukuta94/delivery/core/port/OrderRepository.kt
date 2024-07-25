package github.com.hukuta94.delivery.core.port

import github.com.hukuta94.delivery.core.domain.order.Order

interface OrderRepository : Repository<Order> {

    fun getAllCreated(): Collection<Order>

    fun getAllAssigned(): Collection<Order>

    fun getAllNotCompleted(): Collection<Order>
}