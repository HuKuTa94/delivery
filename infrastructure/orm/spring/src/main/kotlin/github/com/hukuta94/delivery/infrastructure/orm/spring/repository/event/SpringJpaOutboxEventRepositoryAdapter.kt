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
        outboxEventJpaRepository.saveAll(messages as Iterable<OutboxEventMessageJpaEntity>)
    }

    override fun findMessagesInStatuses(statuses: Set<BoxEventMessageStatus>): List<OutboxEventMessageJpaEntity> {
        return outboxEventJpaRepository.findAllByStatusIn(
            statuses = statuses,
            pageable = LIMITED_COUNT_OF_BOX_MESSAGES,
        ).content
    }

    companion object {
        private const val BOX_MESSAGES_LIMIT = 20

        private val LIMITED_COUNT_OF_BOX_MESSAGES = PageRequest.of(
            0,
            BOX_MESSAGES_LIMIT,
            Sort.by(Sort.Direction.ASC, OutboxEventMessageJpaEntity::createdAt.name)
        )
    }
}