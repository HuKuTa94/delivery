package github.com.hukuta94.delivery.infrastructure.adapter.orm.job

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventDeserializer
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventPublisher
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.box.OutboxJpaEntity
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.jpa.OutboxJpaRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDateTime

class PollToPublishOutboxMessagesJob(
    private val outboxJpaRepository: OutboxJpaRepository,
    private val domainEventPublisher: DomainEventPublisher,
    private val domainEventDeserializer: DomainEventDeserializer,
) {

    @Scheduled(fixedDelay = 5000)
    fun execute() {
        val outboxMessages = outboxJpaRepository.findAllByProcessedAtIsNull(LIMITED_COUNT_OF_OUTBOX_MESSAGES).content

        val processedOutboxMessages = outboxMessages.mapNotNull { outboxMessage ->
            processDomainEventOfOutboxMessage(outboxMessage)
        }

        if (processedOutboxMessages.isEmpty()) {
            return
        }

        outboxJpaRepository.saveAll(processedOutboxMessages)
    }

    /**
     * @return successfully processed outbox message [OutboxJpaEntity] or null if it was processed with error
     */
    private fun processDomainEventOfOutboxMessage(outboxMessage: OutboxJpaEntity): OutboxJpaEntity? {
        val domainEvent = outboxMessage.toEvent(domainEventDeserializer)

        return try {
            domainEventPublisher.publish(domainEvent)
            outboxMessage.processedAt = LocalDateTime.now()
            outboxMessage
        } catch (ex: Exception) {
            LOG.error(
                "Domain event of type ${outboxMessage.eventType} from outbox message " +
                    "with id: ${outboxMessage.id} was processed with error: " + ex.message
            )
            return null
        }
    }

    companion object {
        private const val OUTBOX_MESSAGES_LIMIT = 20
        private val LIMITED_COUNT_OF_OUTBOX_MESSAGES = PageRequest.of(
            0,
            OUTBOX_MESSAGES_LIMIT,
            Sort.by(Sort.Direction.ASC, OutboxJpaEntity::createdAt.name)
        )

        private val LOG = LoggerFactory.getLogger(PollToPublishOutboxMessagesJob::class.java)
    }
}