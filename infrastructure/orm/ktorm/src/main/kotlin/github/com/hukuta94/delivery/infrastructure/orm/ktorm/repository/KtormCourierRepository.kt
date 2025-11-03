package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository

import arrow.core.getOrElse
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierName
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Transport
import github.com.hukuta94.delivery.core.domain.common.Location
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.CourierStatusTable
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.CourierTransportTable
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.CourierTable
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.util.UUID
import kotlin.text.split
import kotlin.text.toInt

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
            set(it.name, aggregate.name.value)
            set(it.location, aggregate.location.stringValue())
            set(it.statusId, aggregate.status.id)
            set(it.transportId, aggregate.transport.id)
        }
    }

    override fun update(aggregate: Courier) {
        database.update(CourierTable) {
            set(it.name, aggregate.name.value)
            set(it.location, aggregate.location.stringValue())
            set(it.statusId, aggregate.status.id)
            set(it.transportId, aggregate.transport.id)
            where { it.id eq aggregate.id }
        }
    }

    override fun update(aggregates: Collection<Courier>) {
        //TODO batchUpdate
        aggregates.forEach { update(it) }
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
        id = row[CourierTable.id]!!,
        name = CourierName.of(row[CourierTable.name]!!)
            .getOrElse { error ->
                throw IllegalStateException("Invalid location after move: $error")
           },
        //TODO вынести общую логику в модуль orm:commons
        location = row[CourierTable.location]!!.let { rawLocation ->
            val coordinate = rawLocation.split(',')
            Location.of(
                x = coordinate[0].toInt(),
                y = coordinate[1].toInt(),
            ).getOrElse { error(it.message) }
        },
        status = CourierStatus.from(row[CourierStatusTable.id]!!),
        transport = Transport.from(row[CourierTransportTable.id]!!),
    )

}