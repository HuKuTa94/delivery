package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.port.repository.event.OutboxEventRepositoryPort
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.OutboxEventMessageTable
import org.ktorm.database.Database
import org.ktorm.dsl.batchInsert

class KtormOutboxEventRepository(
    database: Database,
    eventSerializer: ApplicationEventSerializer,
) : OutboxEventRepositoryPort, KtormBoxEventRepository(
    database,
    eventSerializer,
    OutboxEventMessageTable
) {

    override fun saveDomainEvents(domainEvents: Collection<DomainEvent>) {
        database.batchInsert(table) {
            domainEvents.forEach { event ->
                item {
                    setInsertColumns(event)
                }
            }
        }
    }
}
