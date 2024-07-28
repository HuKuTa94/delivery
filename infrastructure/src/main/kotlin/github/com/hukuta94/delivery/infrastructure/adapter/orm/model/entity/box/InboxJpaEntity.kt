package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.box

import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEvent
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventClassType
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventSerializer
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.IntegrationEventClassTypeConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.box.InboxJpaEntity.Companion.TABLE_NAME
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = TABLE_NAME)
class InboxJpaEntity {

    @Id
    lateinit var id: UUID

    @Column(name = "event_type")
    @Convert(converter = IntegrationEventClassTypeConverter::class)
    lateinit var eventType: IntegrationEventClassType

    @Column(name = "payload")
    lateinit var payload: String

    @Column(name = "created_at")
    lateinit var createdAt: LocalDateTime

    @Column(name = "processed_at")
    var processedAt: LocalDateTime? = null

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
        ) = InboxJpaEntity().apply {
                id = event.id
                eventType = IntegrationEventClassType(event::class)
                payload = eventSerializer.serialize(event)
                createdAt = LocalDateTime.now()
                processedAt = null
            }
    }
}