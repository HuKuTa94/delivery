package github.com.hukuta94.delivery.core.application.event.inoutbox

import com.fasterxml.jackson.core.JacksonException
import github.com.hukuta94.delivery.core.application.event.ApplicationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.ApplicationEventPublisher
import github.com.hukuta94.delivery.core.application.port.repository.event.BoxEventMessageRelayRepositoryPort
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

/**
 * Abstract base class implementing the Inbox/Outbox event message pattern.
 *
 * This class handles event message processing by fetching pending or failed messages
 * from the storage, deserializing their payloads, publishing events, and updating
 * their delivery status accordingly.
 *
 * Typical usage involves extending this class and invoking [execute] from a scheduled job
 * (e.g., a cron task) to process accumulated messages periodically.
 *
 * @property eventRepository repository for accessing and updating event messages
 * @property eventDeserializer utility for converting serialized payloads into event objects
 * @property eventPublisher component responsible for dispatching deserialized events
 */
abstract class BoxEventMessageRelay(
    private val eventRepository: BoxEventMessageRelayRepositoryPort,
    private val eventDeserializer: ApplicationEventDeserializer,
    private val eventPublisher: ApplicationEventPublisher,
) {
    /**
     * Executes the Inbox/Outbox processing workflow.
     *
     * Retrieves all messages pending processing or with previous delivery errors,
     * attempts to deserialize and publish each event, and saves the updated statuses.
     *
     * Intended to be called by subclasses, typically from a scheduled (cron) context.
     */
    protected fun execute() {
        val messages = eventRepository.findMessagesInStatuses(
            BOX_EVENT_MESSAGE_STATUSES_TO_BE_PROCESSED,
        )

        if (messages.isEmpty()) {
            return
        }

        messages.forEach { message ->
            processMessage(message)
        }

        eventRepository.saveAll(messages)
    }

    /**
     * Processes a single event message.
     *
     * Deserializes the event payload, publishes the event, and updates the message status
     * to reflect success or failure. Handles both deserialization and publication errors.
     *
     * @param message the [BoxEventMessage] entity to process
     * @return the same message with an updated status and optional error description
     */
    private fun processMessage(message: BoxEventMessage) {
        val event = try {
            eventDeserializer.deserialize(
                serializedEvent = message.payload,
                eventClassType = message.eventType,
            )
        } catch (ex: JacksonException) {
            val error = "Deserialization error of " +
                "${message.eventType.simpleName} event: " + ex.message

            LOG.error(error)

            message.failedConversion(error)
            return
        }

        @Suppress(
            "TooGenericExceptionCaught",
            "Reason: we do not know that type of exception could be in publish"
        )
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
            BoxEventMessageStatus.TO_BE_PROCESSED,
            BoxEventMessageStatus.DELIVERY_ERROR,
        )

        private val LOG = LoggerFactory.getLogger(BoxEventMessageRelay::class.java)
    }
}
