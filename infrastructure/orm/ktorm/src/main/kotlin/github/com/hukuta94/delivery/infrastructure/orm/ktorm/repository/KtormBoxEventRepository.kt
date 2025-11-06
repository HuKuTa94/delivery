package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessage
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.application.port.repository.event.BoxEventMessageRelayRepositoryPort
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.notNull
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.BoxEventMessageStatusTable
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.BoxEventMessageTable
import org.ktorm.database.Database
import org.ktorm.dsl.QueryRowSet
import org.ktorm.dsl.batchUpdate
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.inList
import org.ktorm.dsl.innerJoin
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.dsl.where

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

    override fun findMessagesInStatuses(statuses: Set<BoxEventMessageStatus>): List<BoxEventMessage> {
        if (statuses.isEmpty()) return emptyList()

        return database
            .from(table)
            .innerJoin(BoxEventMessageStatusTable, on = table.statusId eq BoxEventMessageStatusTable.id)
            .select()
            .where { BoxEventMessageStatusTable.id inList statuses.map { it.id } }
            .map { rowToMessage(it) }
    }

    protected fun rowToMessage(row: QueryRowSet): BoxEventMessage =
        BoxEventMessage().apply {
            eventId = row.notNull(table.eventId)
            status = BoxEventMessageStatus.valueOf(row.notNull(BoxEventMessageStatusTable.code))
            version = row.notNull(table.version)
            payload = row.notNull(table.payload)
            eventType = Class.forName(row.notNull(table.eventType)) as Class<out DomainEvent>
            createdAt = row.notNull(table.createdAt)
            processedAt = row[table.processedAt]
            errorDescription = row[table.errorDescription]
        }
}
