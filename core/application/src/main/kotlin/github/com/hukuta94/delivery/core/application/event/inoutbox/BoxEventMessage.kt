package github.com.hukuta94.delivery.core.application.event.inoutbox

import github.com.hukuta94.delivery.core.domain.DomainEvent
import java.time.LocalDateTime
import java.util.*

/**
 * Class describes data of the Inbox/Outbox pattern event message.
 * It is used to increase reliability of processing incoming and outgoing events.
 */
open class BoxEventMessage {

    open lateinit var id: UUID

    open lateinit var payload: String

    open lateinit var createdAt: LocalDateTime

    open var processedAt: LocalDateTime? = null

    open lateinit var eventType: Class<out DomainEvent>

    open var version: Int = 0

    open var status: BoxEventMessageStatus = BoxEventMessageStatus.TO_BE_PROCESSED

    open var errorDescription: String? = null

    internal fun failedConversion(errorDescription: String) {
        this.status = BoxEventMessageStatus.CONVERSION_ERROR
        this.errorDescription = errorDescription
        increaseVersion()
    }

    internal fun failedDelivery(errorDescription: String) {
        this.status = BoxEventMessageStatus.DELIVERY_ERROR
        this.errorDescription = errorDescription
        increaseVersion()
    }

    internal fun successfully(processedAt: LocalDateTime) {
        this.status = BoxEventMessageStatus.SUCCESSFULLY
        this.errorDescription = null
        this.processedAt = processedAt
        increaseVersion()
    }

    private fun increaseVersion() {
        version += 1
    }
}