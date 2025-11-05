package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository

import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierName
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Transport
import github.com.hukuta94.delivery.core.domain.common.Location
import github.com.hukuta94.delivery.infrastructure.orm.commons.fromDb
import github.com.hukuta94.delivery.infrastructure.orm.commons.toDb
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.notNull
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.CourierStatusTable
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.CourierTransportTable
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.CourierTable
import org.ktorm.database.Database
import org.ktorm.dsl.*
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

    override fun add(aggregate: Courier) {
        database.insert(CourierTable) {
            set(it.id, aggregate.id)
            set(it.name, aggregate.name.toDb())
            set(it.location, aggregate.location.toDb())
            set(it.statusId, aggregate.status.id)
            set(it.transportId, aggregate.transport.id)
        }
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
        name = CourierName.fromDb(row.notNull(CourierTable.name)),
        status = CourierStatus.from(row.notNull(CourierStatusTable.id)),
        location = Location.fromDb(row.notNull(CourierTable.location)),
        transport = Transport.from(row.notNull(CourierTransportTable.id)),
    )
}