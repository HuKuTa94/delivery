package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.DomainEvent
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

abstract class ApplicationEventPublisher <EVENT : DomainEvent, HANDLER : ApplicationEventHandler<EVENT>> {

    private val handlerMap = mutableMapOf<KClass<*>, MutableList<HANDLER>>()

    fun registerHandler(handler: HANDLER) {
        handlerMap.computeIfAbsent(handler.eventType) {
            mutableListOf()
        }.add(handler)
    }

    fun publish(event: EVENT) {
        LOG.info("Processing event: $event")

        handlerMap[event::class]?.let { handlers ->
            handleEvents(
                handlers as List<HANDLER>,
                event
            )
        }
    }

    private fun handleEvents(handlers: List<HANDLER>, event: EVENT) {
        handlers.forEach {
            it.handle(event)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ApplicationEventPublisher::class.java)
    }
}