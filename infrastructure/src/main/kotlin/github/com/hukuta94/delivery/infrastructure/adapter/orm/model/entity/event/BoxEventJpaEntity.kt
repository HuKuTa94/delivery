package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event

import github.com.hukuta94.delivery.core.application.event.EventClassType
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class BoxEventJpaEntity<EVENT : Any, EVENT_CLASS_TYPE : EventClassType<EVENT>> {

    @Id
    lateinit var id: UUID

    @Column(name = "payload")
    lateinit var payload: String

    @Column(name = "created_at")
    lateinit var createdAt: LocalDateTime

    @Column(name = "processed_at")
    var processedAt: LocalDateTime? = null

    @get:Column(name = "event_type")
    @set:Column(name = "event_type")
    abstract var eventType: EVENT_CLASS_TYPE
}