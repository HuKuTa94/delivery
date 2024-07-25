package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter

import github.com.hukuta94.delivery.core.application.event.DomainEventSerializer
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.box.OutboxJpaEntity
import java.time.LocalDateTime

class DomainEventConverter(
    private val domainEventSerializer: DomainEventSerializer,
) {

    fun toOutboxJpaEntities(domainEvents: Collection<DomainEvent>): Collection<OutboxJpaEntity> {
        return domainEvents.map { event ->
            val cutClassType = event::class.qualifiedName?.replace(
                DOMAIN_EVENT_PACKAGE,
                ""
            )

            OutboxJpaEntity().apply {
                id = event.id
                type = cutClassType
                content = domainEventSerializer.serialize(event)
                createdAt = LocalDateTime.now()
                processedAt = null
            }
        }
    }

    companion object {
        private val DOMAIN_EVENT_PACKAGE = Regex(".*domain.")
    }
}