package github.com.hukuta94.delivery.core.domain.courier

fun courierName(value: String? = null) = CourierName(
    value = value ?: "A".repeat(CourierName.MIN_NAME_LENGTH)
)