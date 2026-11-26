package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.event

import github.com.hukuta94.delivery.core.application.event.EventSerializer
import github.com.hukuta94.delivery.core.application.port.repository.event.OutboxEventRepositoryPort
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.OutboxEventMessageTable
import org.ktorm.database.Database
import org.ktorm.dsl.batchInsert

class KtormOutboxEventRepository(
    database: Database,
    eventSerializer: EventSerializer,
) : OutboxEventRepositoryPort, KtormBoxEventRepository(
    database,
    eventSerializer,
    OutboxEventMessageTable
) {

    override fun saveDomainEvents(domainEvents: Collection<DomainEvent>) {
        // Ktorm rejects a batch without items; no events is nothing to save, as for the other adapters
        if (domainEvents.isEmpty()) {
            return
        }
        database.batchInsert(table) {
            domainEvents.forEach { event ->
                item {
                    setInsertColumns(event)
                }
            }
        }
    }
}
