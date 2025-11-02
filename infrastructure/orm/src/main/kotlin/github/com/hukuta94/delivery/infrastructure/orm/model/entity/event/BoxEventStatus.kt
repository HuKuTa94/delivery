package github.com.hukuta94.delivery.infrastructure.orm.model.entity.event

enum class BoxEventStatus(
    val dbCode: String, //TODO нормализовать БД и добавить отдельную таблицу со статусами и их id
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
        fun from(dbCode: String) = BoxEventStatus.values()
            .firstOrNull { it.dbCode == dbCode }
            ?: throw IllegalArgumentException(
                "Enum value for ${BoxEventStatus::class.simpleName} is not found by id: $dbCode"
            )
    }
}