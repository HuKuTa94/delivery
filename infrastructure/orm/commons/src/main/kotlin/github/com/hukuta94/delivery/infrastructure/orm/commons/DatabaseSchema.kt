package github.com.hukuta94.delivery.infrastructure.orm.commons

object EnumColumn {
    const val ID = "id"
    const val CODE = "code"
}

object Couriers {
    const val TABLE_NAME = "couriers"

    object Column {
        const val ID = "id"
        const val NAME = "name"
        const val LOCATION = "location"
        const val STATUS_ID = "status_id"
        const val TRANSPORT_ID = "transport_id"
    }
}

object CourierStatuses {
    const val TABLE_NAME = "courier_statuses"
}

object CourierTransports {
    const val TABLE_NAME = "courier_transports"
}

object Orders {
    const val TABLE_NAME = "orders"

    object Column {
        const val ID = "id"
        const val STATUS_ID = "status_id"
        const val COURIER_ID = "courier_id"
        const val LOCATION = "location"
    }
}

object OrderStatuses {
    const val TABLE_NAME = "order_statuses"
}

object Messages {
    object Inbox {
        const val TABLE_NAME = "inbox_messages"
    }

    object Outbox {
        const val TABLE_NAME = "outbox_messages"
    }

    object Column {
        const val EVENT_ID = "event_id"
        const val EVENT_TYPE = "event_type"
        const val PAYLOAD = "payload"
        const val CREATED_AT = "created_at"
        const val PROCESSED_AT = "processed_at"
        const val VERSION = "version"
        const val STATUS_ID = "status_id"
        const val ERROR_DESCRIPTION = "error_description"
    }
}

object MessageStatuses {
    const val TABLE_NAME = "message_statuses"
}
