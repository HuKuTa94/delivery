package github.com.hukuta94.delivery.core.application.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import github.com.hukuta94.delivery.core.domain.DomainEvent

abstract class ApplicationEventSerializer<EVENT : DomainEvent> {

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())

    fun serialize(event: EVENT): String {
        return objectMapper.writeValueAsString(event)
    }
}