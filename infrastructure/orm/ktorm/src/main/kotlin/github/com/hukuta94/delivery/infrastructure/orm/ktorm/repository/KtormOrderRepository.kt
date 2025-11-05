package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository

import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryPort
import github.com.hukuta94.delivery.core.domain.aggregate.order.Order
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.infrastructure.orm.commons.toLocation
import github.com.hukuta94.delivery.infrastructure.orm.commons.toDb
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.notNull
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.OrderStatusTable
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.OrderTable
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.util.*

class KtormOrderRepository(
    private val database: Database,
) : OrderRepositoryPort {

    override fun add(aggregate: Order) {
        database.insert(OrderTable) {
            set(it.id, aggregate.id)
            set(it.courierId, aggregate.courierId)
            set(it.location, aggregate.location.toDb())
            set(it.statusId, aggregate.status.id)
        }
    }

    override fun update(aggregate: Order) {
        database.update(OrderTable) {
            set(it.courierId, aggregate.courierId)
            set(it.location, aggregate.location.toDb())
            set(it.statusId, aggregate.status.id)
            where { it.id eq aggregate.id }
        }
    }

    override fun update(aggregates: Collection<Order>) {
        database.batchUpdate(OrderTable) {
            aggregates.forEach { aggregate ->
                item {
                    set(it.courierId, aggregate.courierId)
                    set(it.location, aggregate.location.toDb())
                    set(it.statusId, aggregate.status.id)
                    where { it.id eq aggregate.id }
                }
            }
        }
    }

    override fun getById(id: UUID): Order =
        database
            .from(OrderTable)
            .innerJoin(OrderStatusTable, on = OrderTable.statusId eq OrderStatusTable.id)
            .select()
            .where { OrderTable.id eq id }
            .map { toOrder(it) }
            .firstOrNull() ?: throw NoSuchElementException("Order $id not found")

    override fun existsById(id: UUID): Boolean =
        database
            .from(OrderTable)
            .select(OrderTable.id)
            .where { OrderTable.id eq id }
            .totalRecordsInAllPages > 0

    override fun getAllCreated(): Collection<Order> =
        findByStatus(OrderStatus.CREATED)

    override fun getAllAssigned(): Collection<Order> =
        findByStatus(OrderStatus.ASSIGNED)

    override fun getAllNotCompleted(): Collection<Order> =
        database
            .from(OrderTable)
            .innerJoin(OrderStatusTable, on = OrderTable.statusId eq OrderStatusTable.id)
            .select()
            .where { OrderStatusTable.code notEq OrderStatus.COMPLETED.name }
            .mapNotNull { toOrder(it) }

    private fun findByStatus(status: OrderStatus): Collection<Order> =
        database
            .from(OrderTable)
            .innerJoin(OrderStatusTable, on = OrderTable.statusId eq OrderStatusTable.id)
            .select()
            .where { OrderStatusTable.code eq status.name }
            .mapNotNull { toOrder(it) }

    private fun toOrder(row: QueryRowSet): Order =
        Order.create(
            id = row.notNull(OrderTable.id),
            status = OrderStatus.from(row.notNull(OrderStatusTable.id)),
            location = row.notNull(OrderTable.location).toLocation(),
            courierId = row[OrderTable.courierId]
        )
}