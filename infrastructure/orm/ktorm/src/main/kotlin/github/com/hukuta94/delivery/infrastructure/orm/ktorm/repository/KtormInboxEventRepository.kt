package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.application.port.repository.event.InboxEventRepositoryPort
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.InboxEventMessageTable
import org.ktorm.database.Database
import org.ktorm.support.postgresql.insertOrUpdate
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class KtormInboxEventRepository(
    database: Database,
    eventSerializer: ApplicationEventSerializer,
) : InboxEventRepositoryPort, KtormBoxEventRepository(
    database,
    eventSerializer,
    InboxEventMessageTable
) {

    override fun saveIntegrationEvent(integrationEvent: DomainEvent) {
        val insertedRows = database.insertOrUpdate(table) {
            set(table.eventId, integrationEvent.eventId)
            set(table.version, 0)
            set(table.statusId, BoxEventMessageStatus.TO_BE_PROCESSED.id)
            set(table.createdAt, LocalDateTime.now())
            set(table.eventType, integrationEvent.javaClass.name)
            set(table.payload, eventSerializer.serialize(integrationEvent))
            onConflict(table.eventId) { doNothing() }
        }

        if (insertedRows == 0) {
            LOG.info("Integration event with id: ${integrationEvent.eventId} skipped because it already exists")
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(KtormInboxEventRepository::class.java)
    }
}
