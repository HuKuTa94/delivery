package github.com.hukuta94.delivery.core.application.port.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.order.Order
import java.util.UUID

interface OrderRepositoryPort : AggregateRepositoryPort<Order, UUID> {

    fun getAllCreated(): Collection<Order>

    fun getAllAssigned(): Collection<Order>

    fun getAllNotCompleted(): Collection<Order>
}
