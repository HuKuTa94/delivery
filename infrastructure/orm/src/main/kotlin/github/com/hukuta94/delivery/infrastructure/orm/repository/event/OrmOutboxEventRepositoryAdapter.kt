package github.com.hukuta94.delivery.infrastructure.orm.repository.event

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventSerializer
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.core.application.port.repository.event.OutboxEventRepositoryPort
import github.com.hukuta94.delivery.infrastructure.orm.model.entity.event.OutboxEventJpaEntity
import java.time.LocalDateTime

class OrmOutboxEventRepositoryAdapter(
    private val outboxEventJpaRepository: OutboxEventJpaRepository,
    private val domainEventSerializer: DomainEventSerializer,
) : OutboxEventRepositoryPort {

    override fun saveDomainEvents(domainEvents: Collection<DomainEvent>) {
        if (domainEvents.isEmpty()) {
            return
        }

        val outboxMessages = domainEvents.map { event ->
            OutboxEventJpaEntity.fromEvent(
                event,
                domainEventSerializer,
                createdAt = LocalDateTime.now(),
            )
        }

        outboxEventJpaRepository.saveAll(outboxMessages)
    }
}