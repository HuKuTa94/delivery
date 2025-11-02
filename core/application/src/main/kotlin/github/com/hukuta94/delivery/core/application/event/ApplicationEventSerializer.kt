package github.com.hukuta94.delivery.core.application.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import github.com.hukuta94.delivery.core.domain.DomainEvent

class ApplicationEventSerializer {

    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    fun serialize(event: DomainEvent): String {
        return objectMapper.writeValueAsString(event)
    }
}