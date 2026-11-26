package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.domain

import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryPort
import github.com.hukuta94.delivery.core.domain.aggregate.order.Order
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.infrastructure.orm.commons.toLocation
import github.com.hukuta94.delivery.infrastructure.orm.commons.toDb
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.notNull
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.OrderTable
import org.ktorm.database.Database
import org.ktorm.dsl.QueryRowSet
import org.ktorm.dsl.batchUpdate
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.inList
import org.ktorm.dsl.map
import org.ktorm.dsl.mapNotNull
import org.ktorm.dsl.select
import org.ktorm.dsl.update
import org.ktorm.dsl.where
import org.ktorm.support.postgresql.insertOrUpdate
import java.util.UUID

class KtormOrderRepository(
    private val database: Database,
) : OrderRepositoryPort {

    override fun add(aggregate: Order): Boolean {
        val insertedRows = database.insertOrUpdate(OrderTable) {
            set(it.id, aggregate.id)
            set(it.courierId, aggregate.courierId)
            set(it.location, aggregate.location.toDb())
            set(it.statusId, aggregate.status.id)
            onConflict(it.id) { doNothing() }
        }
        return insertedRows > 0
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
        // Ktorm rejects a batch without items; no orders is nothing to update, as for the other adapters
        if (aggregates.isEmpty()) {
            return
        }
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
            .select()
            .where { OrderTable.statusId inList listOf(OrderStatus.CREATED.id, OrderStatus.ASSIGNED.id) }
            .mapNotNull { toOrder(it) }

    private fun findByStatus(status: OrderStatus): Collection<Order> =
        database
            .from(OrderTable)
            .select()
            .where { OrderTable.statusId eq status.id }
            .mapNotNull { toOrder(it) }

    private fun toOrder(row: QueryRowSet): Order =
        Order.create(
            id = row.notNull(OrderTable.id),
            status = OrderStatus.from(row.notNull(OrderTable.statusId)),
            location = row.notNull(OrderTable.location).toLocation(),
            courierId = row[OrderTable.courierId]
        )
}
