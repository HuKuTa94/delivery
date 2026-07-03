package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessage
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.application.port.repository.event.BoxEventMessageRelayRepositoryPort
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.notNull
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.BoxEventMessageTable
import org.ktorm.database.Database
import org.ktorm.dsl.QueryRowSet
import org.ktorm.dsl.asc
import org.ktorm.dsl.batchUpdate
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.inList
import org.ktorm.dsl.limit
import org.ktorm.dsl.map
import org.ktorm.dsl.orderBy
import org.ktorm.dsl.select
import org.ktorm.dsl.where
import org.ktorm.support.postgresql.LockingMode
import org.ktorm.support.postgresql.LockingWait
import org.ktorm.support.postgresql.locking

abstract class KtormBoxEventRepository(
    protected val database: Database,
    protected val eventSerializer: ApplicationEventSerializer,
    protected val table: BoxEventMessageTable,
) : BoxEventMessageRelayRepositoryPort {

    override fun saveAll(messages: List<BoxEventMessage>) {
        database.batchUpdate(table) {
            messages.forEach { message ->
                item {
                    set(table.version, message.version)
                    set(table.statusId, message.status.id)
                    set(table.eventType, message.eventType.name)
                    set(table.payload, message.payload)
                    set(table.errorDescription, message.errorDescription)
                    set(table.createdAt, message.createdAt)
                    set(table.processedAt, message.processedAt)
                    where { table.eventId eq message.eventId }
                }
            }
        }
    }

    override fun findMessagesInStatuses(
        statuses: Set<BoxEventMessageStatus>,
        batchSize: Int,
    ): List<BoxEventMessage> {
        if (statuses.isEmpty()) return emptyList()

        return database
            .from(table)
            .select()
            .where { table.statusId inList statuses.map { it.id } }
            .orderBy(table.createdAt.asc())
            .limit(batchSize)
            .locking(LockingMode.FOR_UPDATE, wait = LockingWait.SKIP_LOCKED)
            .map { rowToMessage(it) }
    }

    protected fun rowToMessage(row: QueryRowSet): BoxEventMessage {
        val eventType = Class
            .forName(row.notNull(table.eventType))
            .asSubclass(DomainEvent::class.java)

        return BoxEventMessage().apply {
            eventId = row.notNull(table.eventId)
            status = BoxEventMessageStatus.from(row.notNull(table.statusId))
            version = row.notNull(table.version)
            payload = row.notNull(table.payload)
            this.eventType = eventType
            createdAt = row.notNull(table.createdAt)
            processedAt = row[table.processedAt]
            errorDescription = row[table.errorDescription]
        }
    }
}
