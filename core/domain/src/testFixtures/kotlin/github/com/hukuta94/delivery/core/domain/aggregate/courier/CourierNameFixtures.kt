package github.com.hukuta94.delivery.core.domain.aggregate.courier

import github.com.hukuta94.delivery.core.domain.CourierNameSpecification.MIN_COURIER_NAME_LENGTH

fun courierName(value: String? = null) = CourierName(
    value = value ?: "A".repeat(MIN_COURIER_NAME_LENGTH)
)