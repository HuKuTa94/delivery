package github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.entity.event

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.entity.event.OutboxEventMessageJpaEntity.Companion.TABLE_NAME
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = TABLE_NAME)
class OutboxEventMessageJpaEntity: BoxEventMessageJpaEntity() {

    companion object {
        const val TABLE_NAME = "out_box"

        fun fromEvent(
            event: DomainEvent,
            eventSerializer: ApplicationEventSerializer,
            createdAt: LocalDateTime,
        ) = OutboxEventMessageJpaEntity().apply {
                id = event.eventId
                eventType = event::class.java
                payload = eventSerializer.serialize(event)
                this.createdAt = createdAt
            }
    }
}