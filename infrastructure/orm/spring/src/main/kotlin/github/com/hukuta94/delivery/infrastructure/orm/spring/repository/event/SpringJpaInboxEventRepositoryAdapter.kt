package github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessage
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.application.port.repository.event.BoxEventMessageRelayRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.event.InboxEventRepositoryPort
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.spring.model.entity.event.InboxEventMessageJpaEntity
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.time.LocalDateTime

class SpringJpaInboxEventRepositoryAdapter(
    private val inboxJpaRepository: SpringJpaInboxEventRepository,
    private val eventSerializer: ApplicationEventSerializer
) : InboxEventRepositoryPort, BoxEventMessageRelayRepositoryPort {

    override fun saveIntegrationEvent(integrationEvent: DomainEvent) {
        // Don't save duplicates
        val integrationEventExists = inboxJpaRepository.existsById(integrationEvent.eventId)
        if (integrationEventExists) {
            LOG.info(
                "Integration event with id: ${integrationEvent.eventId} skipped because it already exists"
            )
            return
        }

        val inboxMessage = InboxEventMessageJpaEntity.fromEvent(
            integrationEvent,
            eventSerializer,
            createdAt = LocalDateTime.now()
        )

        inboxJpaRepository.save(inboxMessage)
    }

    override fun saveAll(messages: List<BoxEventMessage>) {
        inboxJpaRepository.saveAll(messages as Iterable<InboxEventMessageJpaEntity>)
    }

    override fun findMessagesInStatuses(statuses: Set<BoxEventMessageStatus>): List<InboxEventMessageJpaEntity> {
        return inboxJpaRepository.findAllByStatusIn(
            statuses = statuses,
            pageable = LIMITED_COUNT_OF_BOX_MESSAGES
        ).content
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(SpringJpaInboxEventRepositoryAdapter::class.java)

        private const val BOX_MESSAGES_LIMIT = 20

        private val LIMITED_COUNT_OF_BOX_MESSAGES = PageRequest.of(
            0,
            BOX_MESSAGES_LIMIT,
            Sort.by(Sort.Direction.ASC, InboxEventMessageJpaEntity::createdAt.name)
        )
    }
}