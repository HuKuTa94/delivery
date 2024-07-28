package github.com.hukuta94.delivery.core.application.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

abstract class ApplicationEventSerializer<EVENT : Any> {

    protected abstract val eventPackage: String

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())

    fun serialize(event: EVENT): String {
        return objectMapper.writeValueAsString(event)
    }

    @Suppress("UNCHECKED_CAST")
    fun deserialize(serializedEvent: String, type: String): EVENT {
        val fullClassType = eventPackage + type
        val eventClass = Class.forName(fullClassType)

        return objectMapper.readValue(serializedEvent, eventClass) as EVENT
    }
}