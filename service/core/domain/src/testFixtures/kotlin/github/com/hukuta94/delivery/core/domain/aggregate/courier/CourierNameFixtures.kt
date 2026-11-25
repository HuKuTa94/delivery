package github.com.hukuta94.delivery.core.domain.aggregate.courier

import arrow.core.getOrElse
import github.com.hukuta94.delivery.core.domain.CourierNameSpecification.ALLOWED_LETTERS
import github.com.hukuta94.delivery.core.domain.CourierNameSpecification.MIN_COURIER_NAME_LENGTH

fun allowedLetter() = ALLOWED_LETTERS.first().toString()

fun courierName(
    value: String = allowedLetter().repeat(MIN_COURIER_NAME_LENGTH),
) = CourierName.of(
    value = value,
).getOrElse { error("Invalid courier name in test fixture") }