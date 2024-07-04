package github.com.hukuta94.delivery.core.port

import github.com.hukuta94.delivery.core.domain.order.Order
import java.util.UUID

interface OrderRepository {

    fun add(order: Order)

    fun update(order: Order)

    fun update(orders: List<Order>)

    fun getById(id: UUID): Order

    fun getAllCreated(): List<Order>

    fun getAllAssigned(): List<Order>

    fun getAllNotCompleted(): List<Order>
}