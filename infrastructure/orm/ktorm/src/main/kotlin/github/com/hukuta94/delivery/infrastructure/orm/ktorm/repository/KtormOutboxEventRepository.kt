package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessage
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.application.port.repository.event.BoxEventMessageRelayRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.event.OutboxEventRepositoryPort
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.BoxEventMessageStatusTable
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.InboxEventMessageTable
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.OutboxEventMessageTable
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.time.LocalDateTime

class KtormOutboxEventRepository(
    private val database: Database,
    private val eventSerializer: ApplicationEventSerializer,
) : OutboxEventRepositoryPort, BoxEventMessageRelayRepositoryPort {

    override fun saveDomainEvents(domainEvents: Collection<DomainEvent>) {
        //TODO batchUpdate
        domainEvents.forEach { event ->
            database.insert(OutboxEventMessageTable) {
                set(it.id, event.eventId)
                set(it.version, 0)
                set(it.statusId, BoxEventMessageStatus.TO_BE_PROCESSED.id)
                set(it.createdAt, LocalDateTime.now())
                set(it.eventType, event.javaClass.name)
                set(it.payload, eventSerializer.serialize(event))
            }
        }
    }

    override fun saveAll(messages: List<BoxEventMessage>) {
        messages.forEach { message ->
            val exists = database
                .from(OutboxEventMessageTable)
                .select(OutboxEventMessageTable.id)
                .where { OutboxEventMessageTable.id eq message.id }
                .totalRecordsInAllPages > 0

            if (exists) {
                //TODO batchUpdate
                database.update(OutboxEventMessageTable) {
                    set(it.version, message.version)
                    set(it.statusId, message.status.id)
                    set(it.eventType, message.eventType.name)
                    set(it.payload, message.payload)
                    set(it.errorDescription, message.errorDescription)
                    set(it.createdAt, message.createdAt)
                    set(it.processedAt, message.processedAt)
                    where { it.id eq message.id }
                }
            } else {
                //TODO batchInsert
                database.insert(OutboxEventMessageTable) {
                    set(it.id, message.id)
                    set(it.version, message.version)
                    set(it.statusId, message.status.id)
                    set(it.eventType, message.eventType.name)
                    set(it.payload, message.payload)
                    set(it.errorDescription, message.errorDescription)
                    set(it.createdAt, message.createdAt)
                    set(it.processedAt, message.processedAt)
                }
            }
        }
    }

    override fun findMessagesInStatuses(statuses: Set<BoxEventMessageStatus>): List<BoxEventMessage> {
        if (statuses.isEmpty()) return emptyList()

        val codes = statuses.map { it.name }

        return database
            .from(OutboxEventMessageTable)
            .innerJoin(
                BoxEventMessageStatusTable,
                on = OutboxEventMessageTable.statusId eq BoxEventMessageStatusTable.id
            )
            .select()
            .where { BoxEventMessageStatusTable.code inList codes }
            .map { rowToMessage(it) }
    }

    private fun rowToMessage(row: QueryRowSet): BoxEventMessage =
        BoxEventMessage().apply {
            id = row[OutboxEventMessageTable.id]!!
            eventType = Class.forName(row[OutboxEventMessageTable.eventType]!!) as Class<out DomainEvent>
            payload = row[OutboxEventMessageTable.payload]!!
            status = BoxEventMessageStatus.valueOf(row[BoxEventMessageStatusTable.code]!!)
            errorDescription = row[OutboxEventMessageTable.errorDescription]
            createdAt = row[OutboxEventMessageTable.createdAt]!!
            processedAt = row[OutboxEventMessageTable.processedAt]
            version = row[OutboxEventMessageTable.version]!!
        }
}