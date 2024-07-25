package github.com.hukuta94.delivery.core.application.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import github.com.hukuta94.delivery.core.domain.DomainEvent

class DomainEventSerializer {

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())

    fun serialize(domainEvent: DomainEvent): String {
        return objectMapper.writeValueAsString(domainEvent)
    }

    fun deserialize(serializedDomainEvent: String, type: String): DomainEvent {
        val fullClassType = DOMAIN_EVENT_PACKAGE + type
        val domainEventClass = Class.forName(fullClassType)

        return objectMapper.readValue(serializedDomainEvent, domainEventClass) as DomainEvent
    }

    companion object {
        const val DOMAIN_EVENT_PACKAGE = "github.com.hukuta94.delivery.core.domain."
    }
}