package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.box

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventSerializer
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.box.OutboxJpaEntity
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.jpa.OutboxJpaRepository

class OrmOutboxRepository(
    private val outboxJpaRepository: OutboxJpaRepository,
    private val domainEventSerializer: DomainEventSerializer,
) {
    fun saveDomainEvents(domainEvents: Collection<DomainEvent>) {
        if (domainEvents.isEmpty()) {
            return
        }

        val outboxMessages = domainEvents.map { event ->
            OutboxJpaEntity.fromEvent(event, domainEventSerializer)
        }

        outboxJpaRepository.saveAll(outboxMessages)
    }
}