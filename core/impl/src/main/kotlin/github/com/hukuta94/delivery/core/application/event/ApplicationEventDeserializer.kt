package github.com.hukuta94.delivery.core.application.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

abstract class ApplicationEventDeserializer<EVENT : Any> {

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())

    @Suppress("UNCHECKED_CAST")
    fun deserialize(
        serializedEvent: String,
        eventClassType: EventClassType<EVENT>
    ): EVENT {
        return objectMapper.readValue(serializedEvent, eventClassType.eventClass.java) as EVENT
    }
}