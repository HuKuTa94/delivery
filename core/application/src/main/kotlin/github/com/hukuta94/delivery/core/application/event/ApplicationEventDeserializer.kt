package github.com.hukuta94.delivery.core.application.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import github.com.hukuta94.delivery.core.domain.DomainEvent

abstract class ApplicationEventDeserializer<EVENT : DomainEvent> {

    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    @Suppress("UNCHECKED_CAST")
    fun deserialize(
        serializedEvent: String,
        eventClassType: EventClassType<EVENT>
    ): EVENT {
        return objectMapper.readValue(serializedEvent, eventClassType.value.java) as EVENT
    }
}