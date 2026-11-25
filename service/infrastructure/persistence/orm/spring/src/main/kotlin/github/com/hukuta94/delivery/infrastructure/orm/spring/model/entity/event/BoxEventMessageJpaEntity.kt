package github.com.hukuta94.delivery.infrastructure.orm.spring.model.entity.event

import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessage
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.commons.Messages.Column.CREATED_AT
import github.com.hukuta94.delivery.infrastructure.orm.commons.Messages.Column.ERROR_DESCRIPTION
import github.com.hukuta94.delivery.infrastructure.orm.commons.Messages.Column.EVENT_ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Messages.Column.EVENT_TYPE
import github.com.hukuta94.delivery.infrastructure.orm.commons.Messages.Column.PAYLOAD
import github.com.hukuta94.delivery.infrastructure.orm.commons.Messages.Column.PROCESSED_AT
import github.com.hukuta94.delivery.infrastructure.orm.commons.Messages.Column.STATUS_ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Messages.Column.VERSION
import github.com.hukuta94.delivery.infrastructure.orm.spring.model.converter.BoxEventMessageStatusConverter
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
    @Column(name = EVENT_ID)
    override lateinit var eventId: UUID

    @Column(name = PAYLOAD)
    override lateinit var payload: String

    @Column(name = CREATED_AT)
    override lateinit var createdAt: LocalDateTime

    @Column(name = PROCESSED_AT)
    override var processedAt: LocalDateTime? = null

    @get:Column(name = EVENT_TYPE)
    override lateinit var eventType: Class<out DomainEvent>

    @Column(name = VERSION)
    override var version: Int = 0

    @Column(name = STATUS_ID)
    @Convert(converter = BoxEventMessageStatusConverter::class)
    override var status: BoxEventMessageStatus = BoxEventMessageStatus.TO_BE_PROCESSED

    @Column(name = ERROR_DESCRIPTION)
    override var errorDescription: String? = null
}
