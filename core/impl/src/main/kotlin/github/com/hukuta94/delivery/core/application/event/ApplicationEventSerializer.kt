package github.com.hukuta94.delivery.core.application.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

abstract class ApplicationEventSerializer<EVENT : Any> {

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())

    fun serialize(event: EVENT): String {
        return objectMapper.writeValueAsString(event)
    }
}