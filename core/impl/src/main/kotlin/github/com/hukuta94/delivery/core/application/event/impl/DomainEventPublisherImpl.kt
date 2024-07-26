package github.com.hukuta94.delivery.core.application.event.impl

import github.com.hukuta94.delivery.core.application.event.DomainEventListener
import github.com.hukuta94.delivery.core.application.event.DomainEventPublisher
import github.com.hukuta94.delivery.core.domain.DomainEvent
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class DomainEventPublisherImpl : DomainEventPublisher {

    private val listenerMap = mutableMapOf<KClass<*>, MutableList<DomainEventListener<out DomainEvent>>>()

    fun registerListener(listener: DomainEventListener<out DomainEvent>) {
        listenerMap.computeIfAbsent(listener.eventType) {
            mutableListOf()
        }.add(listener)
    }

    @Suppress("UNCHECKED_CAST")
    override fun publish(domainEvent: DomainEvent) {
        LOG.info("Processing domain event: $domainEvent")

        listenerMap[domainEvent::class]?.let { listeners ->
            handleDomainEvents(
                listeners as List<DomainEventListener<in DomainEvent>>,
                domainEvent
            )
        }
    }

    private fun handleDomainEvents(
        listeners: List<DomainEventListener<in DomainEvent>>,
        domainEvent: DomainEvent
    ) {
        listeners.forEach {
            it.handle(domainEvent)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(DomainEventPublisherImpl::class.java)
    }
}
