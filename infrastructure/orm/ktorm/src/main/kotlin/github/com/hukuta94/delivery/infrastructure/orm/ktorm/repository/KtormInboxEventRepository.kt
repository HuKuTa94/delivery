package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessage
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.application.port.repository.event.BoxEventMessageRelayRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.event.InboxEventRepositoryPort
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.BoxEventMessageStatusTable
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.InboxEventMessageTable
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class KtormInboxEventRepository(
    private val database: Database,
    private val eventSerializer: ApplicationEventSerializer,
) : InboxEventRepositoryPort, BoxEventMessageRelayRepositoryPort {

    override fun saveIntegrationEvent(integrationEvent: DomainEvent) {
        val integrationEventExists = database.from(InboxEventMessageTable)
            .select()
            .where(InboxEventMessageTable.id eq integrationEvent.eventId)
            .totalRecordsInAllPages > 0

        if (integrationEventExists) {
            LOG.info(
                "Integration event with id: ${integrationEvent.eventId} skipped because it already exists"
            )
            return
        }

        database.insert(InboxEventMessageTable) {
            set(it.id, integrationEvent.eventId)
            set(it.version, 0)
            set(it.statusId, BoxEventMessageStatus.TO_BE_PROCESSED.id)
            set(it.createdAt, LocalDateTime.now())
            set(it.eventType, integrationEvent.javaClass.name)
            set(it.payload, eventSerializer.serialize(integrationEvent))
        }
    }

    override fun saveAll(messages: List<BoxEventMessage>) {
        database.batchUpdate(InboxEventMessageTable) {
            messages.forEach { message ->
                item {
                    set(it.version, message.version)
                    set(it.statusId, message.status.id)
                    set(it.eventType, message.eventType.name)
                    set(it.payload, message.payload)
                    set(it.errorDescription, message.errorDescription)
                    set(it.createdAt, message.createdAt)
                    set(it.processedAt, message.processedAt)
                    where { it.id eq message.id }
                }
            }
        }
    }

    override fun findMessagesInStatuses(statuses: Set<BoxEventMessageStatus>): List<BoxEventMessage> {
        if (statuses.isEmpty()) return emptyList()

        val codes = statuses.map { it.name }

        return database
            .from(InboxEventMessageTable)
            .innerJoin(
                BoxEventMessageStatusTable,
                on = InboxEventMessageTable.statusId eq BoxEventMessageStatusTable.id
            )
            .select()
            .where { BoxEventMessageStatusTable.code inList codes }
            .map { rowToMessage(it) }
    }

    private fun rowToMessage(row: QueryRowSet): BoxEventMessage =
        BoxEventMessage().apply {
            id = row[InboxEventMessageTable.id]!!
            eventType = Class.forName(row[InboxEventMessageTable.eventType]!!) as Class<out DomainEvent>
            payload = row[InboxEventMessageTable.payload]!!
            status = BoxEventMessageStatus.valueOf(row[BoxEventMessageStatusTable.code]!!)
            errorDescription = row[InboxEventMessageTable.errorDescription]
            createdAt = row[InboxEventMessageTable.createdAt]!!
            processedAt = row[InboxEventMessageTable.processedAt]
            version = row[InboxEventMessageTable.version]!!
        }

    companion object {
        private val LOG = LoggerFactory.getLogger(KtormInboxEventRepository::class.java)
    }
}