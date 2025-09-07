package github.com.hukuta94.delivery.core.domain.aggregate.courier

import github.com.hukuta94.delivery.core.domain.ValueObject

data class CourierName private constructor(
    val value: String,
) : ValueObject {
    companion object {
        operator fun invoke(value: String): CourierName {
            require(value.isNotBlank()) {
                "Courier name can not be empty or blank"
            }

            require(value.length >= MIN_NAME_LENGTH) {
                "Courier name is too short. Minimal length must be $MIN_NAME_LENGTH. Actual length of \"$value\" is ${value.length}"
            }

            val trimmedName = value.trim()
            require(trimmedName.length <= MAX_NAME_LENGTH) {
                "Courier name is too long. Maximal length must be $MAX_NAME_LENGTH. Actual length of \"$trimmedName\" is ${trimmedName.length}"
            }

            return CourierName(trimmedName)
        }

        private const val MIN_NAME_LENGTH = 2
        private const val MAX_NAME_LENGTH = 30
    }
}
