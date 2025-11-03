package github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.entity.event

import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessage
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.converter.BoxEventMessageStatusConverter
import java.time.LocalDateTime
import java.util.*
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

/**
 * Entity describes columns table of the In/Out Box pattern.
 * It is used to increase reliability of processing incoming and outgoing events.
 */
@MappedSuperclass
abstract class BoxEventMessageJpaEntity : BoxEventMessage() {

    @Id
    override lateinit var id: UUID

    @Column(name = "payload")
    override lateinit var payload: String

    @Column(name = "created_at")
    override lateinit var createdAt: LocalDateTime

    @Column(name = "processed_at")
    override var processedAt: LocalDateTime? = null

    @get:Column(name = "event_type")
    override lateinit var eventType: Class<out DomainEvent>

    @Column(name = "version")
    override var version: Int = 0

    @Column(name = "status_id")
    @Convert(converter = BoxEventMessageStatusConverter::class)
    override var status: BoxEventMessageStatus = BoxEventMessageStatus.TO_BE_PROCESSED

    @Column(name = "error_description")
    override var errorDescription: String? = null
}