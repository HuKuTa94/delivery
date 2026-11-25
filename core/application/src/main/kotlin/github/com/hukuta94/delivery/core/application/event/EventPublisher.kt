package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.DomainEvent
import org.slf4j.LoggerFactory

class EventPublisher(
    handlers: List<EventHandler<out DomainEvent>>,
) {
    private val handlers = handlers
        .groupBy { it.eventType }
        .mapValues { it.value.toList() }

    /**
     * Passes the [event] to every handler registered for its exact type, in registration order.
     *
     * A failing handler doesn't stop the ones after it. Once all handlers have run,
     * the first failure is rethrown with the others attached as suppressed exceptions.
     */
    fun publish(event: DomainEvent) {
        LOG.info("Processing event: $event")

        val failures = handlers[event::class]
            .orEmpty()
            .mapNotNull { it.handleCatching(event) }

        failures.firstOrNull()?.let { first ->
            failures
                .filterNot { it === first }
                .forEach(first::addSuppressed)
            throw first
        }
    }

    @Suppress(
        "TooGenericExceptionCaught",
        "Reason: a handler may throw anything, and the remaining handlers must still run",
    )
    private fun EventHandler<out DomainEvent>.handleCatching(event: DomainEvent): Exception? = try {
        (this as EventHandler<DomainEvent>).handle(event)
        null
    } catch (ex: Exception) {
        ex
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(EventPublisher::class.java)
    }
}
