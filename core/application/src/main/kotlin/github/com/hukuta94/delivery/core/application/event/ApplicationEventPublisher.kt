package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.DomainEvent
import org.slf4j.LoggerFactory

class ApplicationEventPublisher(
    handlers: List<ApplicationEventHandler<out DomainEvent>>,
) {
    private val handlers = handlers
        .groupBy { it.eventType }
        .mapValues { it.value.toList() }

    fun publish(event: DomainEvent) {
        LOG.info("Processing event: $event")

        handlers[event::class]?.let { handlers ->
            handlers.forEach {
                if (it.eventType.isInstance(event)) {
                    (it as ApplicationEventHandler<DomainEvent>).handle(event)
                }
            }
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ApplicationEventPublisher::class.java)
    }
}
