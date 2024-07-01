package github.com.hukuta94.delivery.core.domain.courier

data class CourierName private constructor(
    val value: String,
) {
    companion object {
        operator fun invoke(value: String): CourierName {
            if (value.isBlank()) {
                throw IllegalArgumentException(
                    "Courier name can not be empty or blank"
                )
            }

            if (value.length < MIN_NAME_LENGTH) {
                throw IllegalArgumentException(
                    "Courier name is too short. Minimal length must be $MIN_NAME_LENGTH. Actual length of \"$value\" is ${value.length}"
                )
            }

            val trimmedName = value.trim()
            if (trimmedName.length > MAX_NAME_LENGTH) {
                throw IllegalArgumentException(
                    "Courier name is too long. Maximal length must be $MAX_NAME_LENGTH. Actual length of \"$trimmedName\" is ${trimmedName.length}"
                )
            }

            return CourierName(trimmedName)
        }

        private const val MIN_NAME_LENGTH = 2
        private const val MAX_NAME_LENGTH = 30
    }
}
