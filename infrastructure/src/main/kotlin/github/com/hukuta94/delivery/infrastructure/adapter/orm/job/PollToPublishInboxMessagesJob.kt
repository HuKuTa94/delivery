package github.com.hukuta94.delivery.infrastructure.adapter.orm.job

import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventPublisher
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event.InboxEventJpaEntity
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.event.InboxEventJpaRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDateTime

class PollToPublishInboxMessagesJob(
    private val inboxJpaRepository: InboxEventJpaRepository,
    private val integrationEventPublisher: IntegrationEventPublisher,
    private val integrationEventDeserializer: IntegrationEventDeserializer,
) {

    @Scheduled(fixedDelay = 5000)
    fun execute() {
        val inboxMessages = inboxJpaRepository.findAllByProcessedAtIsNull(LIMITED_COUNT_OF_INBOX_MESSAGES).content

        val processedOutboxMessages = inboxMessages.mapNotNull { outboxMessage ->
            processIntegrationEventOfInboxMessage(outboxMessage)
        }

        if (processedOutboxMessages.isEmpty()) {
            return
        }

        inboxJpaRepository.saveAll(processedOutboxMessages)
    }

    /**
     * @return successfully processed inbox message [InboxEventJpaEntity] or null if it was processed with error
     */
    private fun processIntegrationEventOfInboxMessage(inboxMessage: InboxEventJpaEntity): InboxEventJpaEntity? {
        val domainEvent = inboxMessage.toEvent(integrationEventDeserializer)

        return try {
            integrationEventPublisher.publish(domainEvent)
            inboxMessage.processedAt = LocalDateTime.now()
            inboxMessage
        } catch (ex: Exception) {
            LOG.error(
                "Integration event of type ${inboxMessage.eventType} from inbox message " +
                    "with id: ${inboxMessage.id} was processed with error: " + ex.message
            )
            return null
        }
    }

    companion object {
        private const val INBOX_MESSAGES_LIMIT = 20
        private val LIMITED_COUNT_OF_INBOX_MESSAGES = PageRequest.of(
            0,
            INBOX_MESSAGES_LIMIT,
            Sort.by(Sort.Direction.ASC, InboxEventJpaEntity::createdAt.name)
        )

        private val LOG = LoggerFactory.getLogger(PollToPublishInboxMessagesJob::class.java)
    }
}