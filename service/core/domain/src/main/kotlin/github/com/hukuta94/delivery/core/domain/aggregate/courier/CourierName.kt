package github.com.hukuta94.delivery.core.domain.aggregate.courier

import arrow.core.raise.either
import arrow.core.raise.ensure
import github.com.hukuta94.delivery.core.domain.BusinessError
import github.com.hukuta94.delivery.core.domain.ValueObject

data class CourierName private constructor(
    val value: String,
) : ValueObject {

    companion object {

        private const val MIN_NAME_LENGTH = 2
        private const val MAX_NAME_LENGTH = 30

        fun of(value: String) = either {
            ensure(value.isNotBlank() && value.isNotEmpty()) {
                Error.NameCanNotBeEmpty
            }
            ensure(value.length >= MIN_NAME_LENGTH) {
                Error.NameTooShort(value)
            }

            val trimmedName = value.trim()
            ensure(trimmedName.length <= MAX_NAME_LENGTH) {
                Error.NameTooLong(trimmedName)
            }

            CourierName(trimmedName)
        }
    }

    sealed class Error(
        override val message: String,
    ) : BusinessError {
        data object NameCanNotBeEmpty : Error("Courier name can not be empty or blank")
        data class NameTooLong(val value: String) : Error(
            "Courier name is too long. " +
                    "Maximal length must be $MAX_NAME_LENGTH. " +
                    "Actual length of \"$value\" is ${value.length}"
        )
        data class NameTooShort(val value: String) : Error(
            "Courier name is too short. " +
                    "Minimal length must be $MIN_NAME_LENGTH. " +
                    "Actual length of \"$value\" is ${value.length}"
        )
    }
}
