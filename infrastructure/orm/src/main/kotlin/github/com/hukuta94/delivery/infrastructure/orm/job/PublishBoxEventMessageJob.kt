package github.com.hukuta94.delivery.infrastructure.orm.job

import github.com.hukuta94.delivery.core.application.event.EventClassType
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.model.entity.event.BoxEventJpaEntity
import github.com.hukuta94.delivery.infrastructure.orm.model.entity.event.BoxEventStatus
import github.com.hukuta94.delivery.infrastructure.orm.repository.event.BoxEventJpaRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

abstract class PublishBoxEventMessageJob<
        EVENT: DomainEvent,
        EVENT_CLASS_TYPE: EventClassType<EVENT>,
        JPA_ENTITY: BoxEventJpaEntity<EVENT, EVENT_CLASS_TYPE>>(

    private val eventJpaRepository: BoxEventJpaRepository<JPA_ENTITY>,
) {

    protected abstract val limitedPageOfMessageCount: PageRequest

    protected abstract fun convertMessageToEvent(message: JPA_ENTITY): EVENT

    protected abstract fun publishEvent(event: EVENT)

    protected fun executeJob() {
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
    private fun processMessage(message: JPA_ENTITY): JPA_ENTITY {
        val event = try {
            convertMessageToEvent(message)
        } catch (ex: Exception) {
            val error = "Deserialization error of " +
                "${message.eventType.value.simpleName} event: " + ex.message

            LOG.error(error)

            message.failedConversion(error)
            return message
        }

        return try {
            publishEvent(event)

            message.successfully(LocalDateTime.now())
            message
        } catch (ex: Exception) {
            val error = "Event publication error: " + ex.message

            LOG.error(error)

            message.failedDelivery(error)
            message
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