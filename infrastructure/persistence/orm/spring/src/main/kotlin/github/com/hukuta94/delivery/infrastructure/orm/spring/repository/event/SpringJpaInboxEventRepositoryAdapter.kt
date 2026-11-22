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
        val insertedRows = inboxJpaRepository.insertIfAbsent(
            eventId = integrationEvent.eventId,
            eventType = integrationEvent.javaClass.name,
            payload = eventSerializer.serialize(integrationEvent),
            createdAt = LocalDateTime.now(),
            statusId = BoxEventMessageStatus.TO_BE_PROCESSED.id,
        )

        if (insertedRows == 0) {
            LOG.info("Integration event with id: ${integrationEvent.eventId} skipped because it already exists")
        }
    }

    override fun saveAll(messages: List<BoxEventMessage>) {
        val inboxMessages = messages.map {
            it as? InboxEventMessageJpaEntity
                ?: throw IllegalArgumentException(
                    "Expected InboxEventMessageJpaEntity but got ${it::class.java.name}"
                )
        }

        inboxJpaRepository.saveAll(inboxMessages)
    }

    override fun findMessagesInStatuses(
        statuses: Set<BoxEventMessageStatus>,
        batchSize: Int,
    ): List<InboxEventMessageJpaEntity> {
        val pageable = PageRequest.of(
            0,
            batchSize,
            Sort.by(Sort.Direction.ASC, InboxEventMessageJpaEntity::createdAt.name),
        )
        return inboxJpaRepository.findAllByStatusIn(statuses, pageable)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(SpringJpaInboxEventRepositoryAdapter::class.java)
    }
}
