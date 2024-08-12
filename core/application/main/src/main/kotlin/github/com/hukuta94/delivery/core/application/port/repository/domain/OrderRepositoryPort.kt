package github.com.hukuta94.delivery.core.application.port.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.order.Order

interface OrderRepositoryPort : AggregateRepositoryPort<Order> {

    fun getAllCreated(): Collection<Order>

    fun getAllAssigned(): Collection<Order>

    fun getAllNotCompleted(): Collection<Order>
}