package github.com.hukuta94.delivery.core.application.event.integration

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class IntegrationEventPublisherImpl : IntegrationEventPublisher {

    private val handlerMap = mutableMapOf<KClass<*>, MutableList<IntegrationEventHandler<out IntegrationEvent>>>()

    fun registerHandler(handler: IntegrationEventHandler<out IntegrationEvent>) {
        handlerMap.computeIfAbsent(handler.eventType) {
            mutableListOf()
        }.add(handler)
    }

    @Suppress("UNCHECKED_CAST")
    override fun publish(IntegrationEvent: IntegrationEvent) {
        LOG.info("Processing domain event: $IntegrationEvent")

        handlerMap[IntegrationEvent::class]?.let { listeners ->
            handleIntegrationEvents(
                listeners as List<IntegrationEventHandler<in IntegrationEvent>>,
                IntegrationEvent
            )
        }
    }

    private fun handleIntegrationEvents(
        listeners: List<IntegrationEventHandler<in IntegrationEvent>>,
        IntegrationEvent: IntegrationEvent
    ) {
        listeners.forEach {
            it.handle(IntegrationEvent)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(IntegrationEventPublisherImpl::class.java)
    }
}
