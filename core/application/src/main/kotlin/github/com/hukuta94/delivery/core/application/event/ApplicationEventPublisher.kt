package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.DomainEvent
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class ApplicationEventPublisher(
    handlers: List<ApplicationEventHandler<out DomainEvent>>,
) {
    private val handlers = mutableMapOf<KClass<out DomainEvent>, MutableList<ApplicationEventHandler<out DomainEvent>>>()

    init {
        handlers.forEach { handler ->
            this.handlers.computeIfAbsent(handler.eventType) {
                mutableListOf()
            }.add(handler)
        }
    }

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