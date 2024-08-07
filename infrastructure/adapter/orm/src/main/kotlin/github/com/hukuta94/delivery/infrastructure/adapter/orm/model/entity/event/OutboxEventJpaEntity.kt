package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventClassType
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventDeserializer
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventSerializer
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.DomainEventClassTypeConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event.OutboxEventJpaEntity.Companion.TABLE_NAME
import java.time.LocalDateTime
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = TABLE_NAME)
class OutboxEventJpaEntity: BoxEventJpaEntity<DomainEvent, DomainEventClassType>() {

    @Convert(converter = DomainEventClassTypeConverter::class)
    override lateinit var eventType: DomainEventClassType

    fun toEvent(
        eventDeserializer: DomainEventDeserializer
    ): DomainEvent {
        return eventDeserializer.deserialize(
            serializedEvent = payload,
            eventClassType = eventType,
        )
    }

    companion object {
        const val TABLE_NAME = "dlv_outbox"

        fun fromEvent(
            event: DomainEvent,
            eventSerializer: DomainEventSerializer,
            createdAt: LocalDateTime,
        ) = OutboxEventJpaEntity().apply {
                id = event.id
                eventType = DomainEventClassType(event::class)
                payload = eventSerializer.serialize(event)
                this.createdAt = createdAt
            }
    }
}