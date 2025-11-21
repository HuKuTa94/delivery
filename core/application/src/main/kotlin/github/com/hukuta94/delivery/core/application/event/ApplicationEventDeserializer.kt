package github.com.hukuta94.delivery.core.application.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import github.com.hukuta94.delivery.core.domain.DomainEvent

class ApplicationEventDeserializer {

    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    fun deserialize(
        serializedEvent: String,
        eventClassType: Class<out DomainEvent>
    ): DomainEvent {
        return objectMapper.readValue(serializedEvent, eventClassType)
    }
}
