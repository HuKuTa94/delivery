package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.application.port.repository.event.OutboxEventRepositoryPort
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.OutboxEventMessageTable
import org.ktorm.database.Database
import org.ktorm.dsl.batchInsert
import java.time.LocalDateTime

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
                    set(table.eventId, event.eventId)
                    set(table.version, 0)
                    set(table.statusId, BoxEventMessageStatus.TO_BE_PROCESSED.id)
                    set(table.createdAt, LocalDateTime.now())
                    set(table.eventType, event.javaClass.name)
                    set(table.payload, eventSerializer.serialize(event))
                }
            }
        }
    }
}
