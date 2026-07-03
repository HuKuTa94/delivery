package github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessage
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.application.port.repository.event.BoxEventMessageRelayRepositoryPort
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.core.application.port.repository.event.OutboxEventRepositoryPort
import github.com.hukuta94.delivery.infrastructure.orm.spring.model.entity.event.OutboxEventMessageJpaEntity
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDateTime

class SpringJpaOutboxEventRepositoryAdapter(
    private val outboxEventJpaRepository: SpringJpaOutboxEventRepository,
    private val eventSerializer: ApplicationEventSerializer,
) : OutboxEventRepositoryPort, BoxEventMessageRelayRepositoryPort {

    override fun saveDomainEvents(domainEvents: Collection<DomainEvent>) {
        if (domainEvents.isEmpty()) {
            return
        }

        val outboxMessages = domainEvents.map { event ->
            OutboxEventMessageJpaEntity.fromEvent(
                event,
                eventSerializer,
                createdAt = LocalDateTime.now(),
            )
        }

        outboxEventJpaRepository.saveAll(outboxMessages)
    }

    override fun saveAll(messages: List<BoxEventMessage>) {
        val outboxMessages = messages.map {
            it as? OutboxEventMessageJpaEntity
                ?: throw IllegalArgumentException(
                    "Expected OutboxEventMessageJpaEntity but got ${it::class.java.name}"
                )
        }

        outboxEventJpaRepository.saveAll(outboxMessages)
    }

    override fun findMessagesInStatuses(
        statuses: Set<BoxEventMessageStatus>,
        batchSize: Int,
    ): List<OutboxEventMessageJpaEntity> {
        val pageable = PageRequest.of(
            0,
            batchSize,
            Sort.by(Sort.Direction.ASC, OutboxEventMessageJpaEntity::createdAt.name),
        )
        return outboxEventJpaRepository.findAllByStatusIn(statuses, pageable)
    }
}
