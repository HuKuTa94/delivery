package github.com.hukuta94.delivery.core.domain.aggregate.courier

const val MIN_COURIER_NAME_LENGTH = 2
const val MAX_COURIER_NAME_LENGTH = 30

fun courierName(value: String? = null) = CourierName(
    value = value ?: "A".repeat(MIN_COURIER_NAME_LENGTH)
)