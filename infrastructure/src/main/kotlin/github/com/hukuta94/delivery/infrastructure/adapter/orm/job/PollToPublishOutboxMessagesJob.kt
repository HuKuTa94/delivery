package github.com.hukuta94.delivery.infrastructure.adapter.orm.job

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventDeserializer
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventPublisher
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event.OutboxEventJpaEntity
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event.OutboxEventStatus
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.event.OutboxEventJpaRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDateTime

class PollToPublishOutboxMessagesJob(
    private val outboxEventJpaRepository: OutboxEventJpaRepository,
    private val domainEventPublisher: DomainEventPublisher,
    private val domainEventDeserializer: DomainEventDeserializer,
) {

    //TODO Вынести все магические числа, а так же количество выгружаемых outbox сообщений за раз, в файл конфигурации
    @Scheduled(fixedDelay = 5000)
    fun execute() {
        val outboxMessages = outboxEventJpaRepository.findAllByStatusIn(
            OUTBOX_MESSAGE_STATUS_TO_BE_PROCESSED,
            LIMITED_COUNT_OF_OUTBOX_MESSAGES
        ).content

        if (outboxMessages.isEmpty()) {
            return
        }

        outboxMessages.forEach { outboxMessage ->
            processDomainEventOfOutboxMessage(outboxMessage)
        }

        outboxEventJpaRepository.saveAll(outboxMessages)
    }

    /**
     * @return processed outbox message [OutboxEventJpaEntity] with changed status and error description if it occurs.
     */
    private fun processDomainEventOfOutboxMessage(outboxMessage: OutboxEventJpaEntity): OutboxEventJpaEntity {
        val domainEvent = try {
            outboxMessage.toEvent(domainEventDeserializer)
        } catch (ex: Exception) {
            val error = "Deserialization error of " +
                "${outboxMessage.eventType.value.simpleName} domain event: " + ex.message

            LOG.error(error)

            return outboxMessage.apply {
                errorDescription = error
                status = OutboxEventStatus.CONVERSION_ERROR
            }
        }

        return try {
            domainEventPublisher.publish(domainEvent)

            outboxMessage.apply {
                errorDescription = null // clear error description after successfully publish
                status = OutboxEventStatus.SUCCESSFULLY
                processedAt = LocalDateTime.now()
            }
        } catch (ex: Exception) {
            val error = "Domain event publication error: " + ex.message

            LOG.error(error)

            outboxMessage.apply {
                errorDescription = error
                status = OutboxEventStatus.DELIVERY_ERROR
            }
        }
    }

    companion object {
        private const val OUTBOX_MESSAGES_LIMIT = 20

        private val OUTBOX_MESSAGE_STATUS_TO_BE_PROCESSED = setOf(
            OutboxEventStatus.TO_BE_PROCESSED,
            OutboxEventStatus.DELIVERY_ERROR,
        )

        private val LIMITED_COUNT_OF_OUTBOX_MESSAGES = PageRequest.of(
            0,
            OUTBOX_MESSAGES_LIMIT,
            Sort.by(Sort.Direction.ASC, OutboxEventJpaEntity::createdAt.name)
        )

        private val LOG = LoggerFactory.getLogger(PollToPublishOutboxMessagesJob::class.java)
    }
}