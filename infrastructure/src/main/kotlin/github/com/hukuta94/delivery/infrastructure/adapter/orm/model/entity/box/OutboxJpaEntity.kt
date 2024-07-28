package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.box

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventClassType
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventDeserializer
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventSerializer
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.DomainEventClassTypeConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.box.OutboxJpaEntity.Companion.TABLE_NAME
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = TABLE_NAME)
class OutboxJpaEntity {

    @Id
    var id: UUID? = null

    @Column(name = "type")
    @Convert(converter = DomainEventClassTypeConverter::class)
    lateinit var type: DomainEventClassType

    @Column(name = "content")
    lateinit var content: String

    @Column(name = "created_at")
    lateinit var createdAt: LocalDateTime

    @Column(name = "processed_at")
    var processedAt: LocalDateTime? = null

    fun toEvent(
        eventDeserializer: DomainEventDeserializer
    ): DomainEvent {
        return eventDeserializer.deserialize(
            serializedEvent = content,
            eventClassType = type,
        )
    }

    companion object {
        const val TABLE_NAME = "dlv_outbox"

        fun fromEvent(
            event: DomainEvent,
            eventSerializer: DomainEventSerializer
        ) = OutboxJpaEntity().apply {
            id = event.id
            type = DomainEventClassType(event::class)
            content = eventSerializer.serialize(event)
            createdAt = LocalDateTime.now()
            processedAt = null
        }
    }
}