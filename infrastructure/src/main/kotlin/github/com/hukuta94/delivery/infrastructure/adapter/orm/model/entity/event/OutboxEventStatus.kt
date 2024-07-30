package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event

enum class OutboxEventStatus(
    val dbCode: String,
) {
    /**
     * Event is ready to be processed.
     */
    TO_BE_PROCESSED(dbCode = "TBP"),

    /**
     * Event was processed successfully.
     */
    SUCCESSFULLY(dbCode = "S"),

    /**
     * Event was processed with a conversion error.
     * This error can be reason of failure of serialization or deserialization data.
     */
    CONVERSION_ERROR(dbCode = "CE"),

    /**
     * Event was not delivered to receiver.
     */
    DELIVERY_ERROR(dbCode = "DE"),
    ;

    companion object {
        fun from(dbCode: String) = OutboxEventStatus.values()
            .firstOrNull { it.dbCode == dbCode }
            ?: throw IllegalArgumentException("Enum value for ${OutboxEventStatus::class.simpleName} is not found by id: $dbCode")
    }
}