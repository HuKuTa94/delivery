package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter

import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEvent
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventSerializer
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.box.InboxJpaEntity
import java.time.LocalDateTime

class IntegrationEventConverter(
    private val integrationEventSerializer: IntegrationEventSerializer,
) {

    fun toInboxJpaEntity(integrationEvent: IntegrationEvent): InboxJpaEntity {
        val qualifiedName = integrationEvent::class.qualifiedName ?: error("Qualified name is absent for event class: ${integrationEvent::class.simpleName}")
        val cutClassType = qualifiedName.replace(INTEGRATION_EVENT_PACKAGE, "")

        return InboxJpaEntity().apply {
                id = integrationEvent.id
                type = cutClassType
                content = integrationEventSerializer.serialize(integrationEvent)
                createdAt = LocalDateTime.now()
                processedAt = null
            }
    }

    companion object {
        private val INTEGRATION_EVENT_PACKAGE = Regex(".*integration.")
    }
}