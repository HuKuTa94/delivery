package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.domain

import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Transport
import github.com.hukuta94.delivery.infrastructure.orm.commons.toCourierName
import github.com.hukuta94.delivery.infrastructure.orm.commons.toLocation
import github.com.hukuta94.delivery.infrastructure.orm.commons.toDb
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.notNull
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.CourierStatusTable
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.CourierTransportTable
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.CourierTable
import org.ktorm.database.Database
import org.ktorm.dsl.QueryRowSet
import org.ktorm.dsl.batchUpdate
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.innerJoin
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.update
import org.ktorm.dsl.where
import org.ktorm.support.postgresql.insertOrUpdate
import java.util.UUID

class KtormCourierRepository(
    private val database: Database,
) : CourierRepositoryPort {

    override fun getAllFree(): Collection<Courier> =
        database
            .from(CourierTable)
            .innerJoin(CourierStatusTable, on = CourierTable.statusId eq CourierStatusTable.id)
            .innerJoin(CourierTransportTable, on = CourierTable.transportId eq CourierTransportTable.id)
            .select()
            .where { CourierStatusTable.code eq CourierStatus.FREE.name }
            .map { toCourier(it) }

    override fun getAllBusy(): Collection<Courier> =
        database
            .from(CourierTable)
            .innerJoin(CourierStatusTable, on = CourierTable.statusId eq CourierStatusTable.id)
            .innerJoin(CourierTransportTable, on = CourierTable.transportId eq CourierTransportTable.id)
            .select()
            .where { CourierStatusTable.code eq CourierStatus.BUSY.name }
            .map { toCourier(it) }

    override fun add(aggregate: Courier): Boolean {
        val insertedRows = database.insertOrUpdate(CourierTable) {
            set(it.id, aggregate.id)
            set(it.name, aggregate.name.toDb())
            set(it.location, aggregate.location.toDb())
            set(it.statusId, aggregate.status.id)
            set(it.transportId, aggregate.transport.id)
            onConflict(it.id) { doNothing() }
        }
        return insertedRows > 0
    }

    override fun update(aggregate: Courier) {
        database.update(CourierTable) {
            set(it.name, aggregate.name.toDb())
            set(it.location, aggregate.location.toDb())
            set(it.statusId, aggregate.status.id)
            set(it.transportId, aggregate.transport.id)
            where { it.id eq aggregate.id }
        }
    }

    override fun update(aggregates: Collection<Courier>) {
        // Ktorm rejects a batch without items; no couriers is nothing to update, as for the other adapters
        if (aggregates.isEmpty()) {
            return
        }
        database.batchUpdate(CourierTable) {
            aggregates.forEach { aggregate ->
                item {
                    set(it.name, aggregate.name.toDb())
                    set(it.location, aggregate.location.toDb())
                    set(it.statusId, aggregate.status.id)
                    set(it.transportId, aggregate.transport.id)
                    where { it.id eq aggregate.id }
                }
            }
        }
    }

    override fun getById(id: UUID): Courier =
        database
            .from(CourierTable)
            .innerJoin(CourierStatusTable, on = CourierTable.statusId eq CourierStatusTable.id)
            .innerJoin(CourierTransportTable, on = CourierTable.transportId eq CourierTransportTable.id)
            .select()
            .where { CourierTable.id eq id }
            .map { toCourier(it) }
            .firstOrNull() ?: throw NoSuchElementException("Courier $id not found")

    override fun existsById(id: UUID): Boolean =
        database
            .from(CourierTable)
            .select(CourierTable.id)
            .where { CourierTable.id eq id }
            .totalRecordsInAllPages > 0

    private fun toCourier(row: QueryRowSet) = Courier.create(
        id = row.notNull(CourierTable.id),
        name = row.notNull(CourierTable.name).toCourierName(),
        status = CourierStatus.from(row.notNull(CourierStatusTable.id)),
        location = row.notNull(CourierTable.location).toLocation(),
        transport = Transport.from(row.notNull(CourierTransportTable.id)),
    )
}
