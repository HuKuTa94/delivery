package github.com.hukuta94.delivery.core.application.event.inoutbox

enum class BoxEventMessageStatus(
    val id: Int,
) {
    /**
     * Event is ready to be processed.
     */
    TO_BE_PROCESSED(id = 1),

    /**
     * Event was processed successfully.
     */
    SUCCESSFULLY(id = 2),

    /**
     * Event was processed with a conversion error.
     * This error can be reason of failure of serialization or deserialization data.
     */
    CONVERSION_ERROR(id = 3),

    /**
     * Event was not delivered to receiver.
     */
    DELIVERY_ERROR(id = 4),
    ;

    companion object {
        fun from(id: Int) = entries
            .firstOrNull { it.id == id }
            ?: throw IllegalArgumentException(
                "Enum value for ${BoxEventMessageStatus::class.simpleName} is not found by id: $id"
            )
    }
}