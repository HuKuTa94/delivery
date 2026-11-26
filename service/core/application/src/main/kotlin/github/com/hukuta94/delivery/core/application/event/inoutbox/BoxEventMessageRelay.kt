package github.com.hukuta94.delivery.core.application.event.inoutbox

import com.fasterxml.jackson.core.JacksonException
import github.com.hukuta94.delivery.core.application.event.EventDeserializer
import github.com.hukuta94.delivery.core.application.event.EventPublisher
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
 * Typical usage involves extending this class and invoking [processBatch] from a scheduled job,
 * each batch in its own transaction, as long as it returns `true`.
 *
 * @param eventRepository repository for accessing and updating event messages
 * @param eventDeserializer utility for converting serialized payloads into event objects
 * @param eventPublisher component responsible for dispatching deserialized events
 * @param batchSize maximum number of messages fetched and processed per batch
 */
abstract class BoxEventMessageRelay(
    private val eventRepository: BoxEventMessageRelayRepositoryPort,
    private val eventDeserializer: EventDeserializer,
    private val eventPublisher: EventPublisher,
    private val batchSize: Int,
) {
    /**
     * Processes one batch of the Inbox/Outbox workflow in the caller's transaction.
     *
     * Retrieves up to [batchSize] messages pending processing or with previous delivery errors,
     * attempts to deserialize and publish each event, and saves the updated statuses.
     *
     * @return `true` when more messages may be waiting: the batch was full and every message of it succeeded.
     * After a failure the caller should wait for its next run instead of retrying the failed messages at once
     */
    protected fun processBatch(): Boolean {
        val messages = eventRepository.findMessagesInStatuses(
            BOX_EVENT_MESSAGE_STATUSES_TO_BE_PROCESSED,
            batchSize,
        )

        if (messages.isEmpty()) {
            return false
        }

        messages.forEach { message ->
            processMessage(message)
        }

        eventRepository.saveAll(messages)

        return messages.size == batchSize && messages.all { it.status == BoxEventMessageStatus.SUCCESS }
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
