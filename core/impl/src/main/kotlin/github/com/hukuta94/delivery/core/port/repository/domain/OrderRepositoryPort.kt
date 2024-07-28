package github.com.hukuta94.delivery.core.port.repository.domain

import github.com.hukuta94.delivery.core.domain.order.Order

interface OrderRepositoryPort : AggregateRepository<Order> {

    fun getAllCreated(): Collection<Order>

    fun getAllAssigned(): Collection<Order>

    fun getAllNotCompleted(): Collection<Order>
}