package github.com.hukuta94.delivery.infrastructure.orm.model.entity.event

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.model.entity.event.OutboxEventJpaEntity.Companion.TABLE_NAME
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = TABLE_NAME)
class OutboxEventJpaEntity: BoxEventJpaEntity() {

    companion object {
        const val TABLE_NAME = "dlv_out_box"

        fun fromEvent(
            event: DomainEvent,
            eventSerializer: ApplicationEventSerializer,
            createdAt: LocalDateTime,
        ) = OutboxEventJpaEntity().apply {
                id = event.eventId
                eventType = event::class.java
                payload = eventSerializer.serialize(event)
                this.createdAt = createdAt
            }
    }
}