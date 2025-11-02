package github.com.hukuta94.delivery.infrastructure.orm.job

import github.com.hukuta94.delivery.core.application.event.ApplicationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.ApplicationEventPublisher
import github.com.hukuta94.delivery.infrastructure.orm.model.entity.event.BoxEventJpaEntity
import github.com.hukuta94.delivery.infrastructure.orm.model.entity.event.BoxEventStatus
import github.com.hukuta94.delivery.infrastructure.orm.repository.event.BoxEventJpaRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

abstract class PublishBoxEventMessageJob<BOX_JPA_ENTITY : BoxEventJpaEntity>(
    private val eventJpaRepository: BoxEventJpaRepository<BOX_JPA_ENTITY>,
    private val eventDeserializer: ApplicationEventDeserializer,
    private val eventPublisher: ApplicationEventPublisher,
) {
    protected fun executeJob(limitedPageOfMessageCount: PageRequest) {
        val messages = eventJpaRepository.findAllByStatusIn(
            BOX_EVENT_MESSAGE_STATUSES_TO_BE_PROCESSED,
            limitedPageOfMessageCount,
        ).content

        if (messages.isEmpty()) {
            return
        }

        messages.forEach { message ->
            processMessage(message)
        }

        eventJpaRepository.saveAll(messages)
    }

    /**
     * @return processed message [BoxEventJpaEntity] with changed status and error description if it occurs.
     */
    private fun processMessage(message: BoxEventJpaEntity) {
        val event = try {
            eventDeserializer.deserialize(
                serializedEvent = message.payload,
                eventClassType = message.eventType,
            )
        } catch (ex: Exception) {
            val error = "Deserialization error of " +
                "${message.eventType?.simpleName} event: " + ex.message

            LOG.error(error)

            message.failedConversion(error)
            return
        }

        try {
            eventPublisher.publish(event)
            message.successfully(LocalDateTime.now())
        } catch (ex: Exception) {
            val error = "Event publication error: " + ex.message

            LOG.error(error)

            message.failedDelivery(error)
        }
    }

    companion object {
        private val BOX_EVENT_MESSAGE_STATUSES_TO_BE_PROCESSED = setOf(
            BoxEventStatus.TO_BE_PROCESSED,
            BoxEventStatus.DELIVERY_ERROR,
        )

        private val LOG = LoggerFactory.getLogger(PublishBoxEventMessageJob::class.java)
    }
}