package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository

import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.DomainEventConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.jpa.OutboxJpaRepository

class OrmOutboxRepository(
    private val outboxJpaRepository: OutboxJpaRepository,
    private val domainEventConverter: DomainEventConverter,
) {
    fun saveDomainEvents(domainEvents: Collection<DomainEvent>) {
        if (domainEvents.isEmpty()) {
            return
        }

        val outboxMessages = domainEventConverter.toOutboxJpaEntities(domainEvents)
        outboxJpaRepository.saveAll(outboxMessages)
    }
}