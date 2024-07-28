package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event

import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEvent
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventClassType
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventSerializer
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.IntegrationEventClassTypeConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event.InboxEventJpaEntity.Companion.TABLE_NAME
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = TABLE_NAME)
class InboxEventJpaEntity : BoxEventJpaEntity<IntegrationEvent, IntegrationEventClassType>() {

    @Convert(converter = IntegrationEventClassTypeConverter::class)
    override lateinit var eventType: IntegrationEventClassType

    fun toEvent(
        eventDeserializer: IntegrationEventDeserializer
    ): IntegrationEvent {
        return eventDeserializer.deserialize(
            serializedEvent = payload,
            eventClassType = eventType,
        )
    }

    companion object {
        const val TABLE_NAME = "dlv_inbox"

        fun fromEvent(
            event: IntegrationEvent,
            eventSerializer: IntegrationEventSerializer
        ) = InboxEventJpaEntity().apply {
                id = event.id
                eventType = IntegrationEventClassType(event::class)
                payload = eventSerializer.serialize(event)
                createdAt = LocalDateTime.now()
                processedAt = null
            }
    }
}