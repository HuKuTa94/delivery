package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.event

import github.com.hukuta94.delivery.core.application.event.EventSerializer
import github.com.hukuta94.delivery.core.application.port.repository.event.InboxEventRepositoryPort
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.InboxEventMessageTable
import org.ktorm.database.Database
import org.ktorm.support.postgresql.insertOrUpdate

class KtormInboxEventRepository(
    database: Database,
    eventSerializer: EventSerializer,
) : InboxEventRepositoryPort, KtormBoxEventRepository(
    database,
    eventSerializer,
    InboxEventMessageTable
) {

    override fun saveIntegrationEvent(integrationEvent: DomainEvent): Boolean {
        val insertedRows = database.insertOrUpdate(table) {
            setInsertColumns(integrationEvent)
            onConflict(table.eventId) { doNothing() }
        }
        return insertedRows > 0
    }
}
