package github.com.hukuta94.delivery.infrastructure.orm.model.entity.event

import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.model.converter.BoxEventStatusConverter
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
abstract class BoxEventJpaEntity {

    @Id
    lateinit var id: UUID

    @Column(name = "payload")
    lateinit var payload: String

    @Column(name = "created_at")
    lateinit var createdAt: LocalDateTime

    @Column(name = "processed_at")
    var processedAt: LocalDateTime? = null

    @get:Column(name = "event_type")
    lateinit var eventType: Class<out DomainEvent>

    @Column(name = "version")
    var version: Int = 0

    @Column(name = "status_id")
    @Convert(converter = BoxEventStatusConverter::class)
    var status: BoxEventStatus = BoxEventStatus.TO_BE_PROCESSED

    @Column(name = "error_description")
    var errorDescription: String? = null

    fun failedConversion(errorDescription: String) {
        this.status = BoxEventStatus.CONVERSION_ERROR
        this.errorDescription = errorDescription
        increaseVersion()
    }

    fun failedDelivery(errorDescription: String) {
        this.status = BoxEventStatus.DELIVERY_ERROR
        this.errorDescription = errorDescription
        increaseVersion()
    }

    fun successfully(processedAt: LocalDateTime) {
        this.status = BoxEventStatus.SUCCESSFULLY
        this.errorDescription = null
        this.processedAt = processedAt
        increaseVersion()
    }

    private fun increaseVersion() {
        version += 1
    }
}