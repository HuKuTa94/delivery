package github.com.hukuta94.delivery.core.application.event.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

class IntegrationEventSerializer {

    private val objectMapper = ObjectMapper().registerModule(KotlinModule())

    fun serialize(integrationEvent: IntegrationEvent): String {
        return objectMapper.writeValueAsString(integrationEvent)
    }

    fun deserialize(serializedIntegrationEvent: String, type: String): IntegrationEvent {
        val fullClassType = INTEGRATION_EVENT_PACKAGE + type
        val integrationEventClass = Class.forName(fullClassType)

        return objectMapper.readValue(serializedIntegrationEvent, integrationEventClass) as IntegrationEvent
    }

    companion object {
        const val INTEGRATION_EVENT_PACKAGE = "github.com.hukuta94.delivery.core.application.event.integration."
    }
}