package github.com.hukuta94.delivery.core.domain.common

import github.com.hukuta94.delivery.core.domain.ValueObject

data class Location private constructor(
    val x: Int,
    val y: Int,
) : ValueObject {

    companion object {

        fun random() = Location(
            x = (VALID_COORDINATE_RANGE).random(),
            y = (VALID_COORDINATE_RANGE).random(),
        )

        operator fun invoke(x: Int, y: Int): Location {
            validate(x)
            validate(y)
            return Location(x, y)
        }

        private fun validate(coordinate: Int) {
            require(coordinate in VALID_COORDINATE_RANGE) {
                "Expected coordinate must be between $MIN_COORDINATE_VALUE and $MAX_COORDINATE_VALUE. Actual coordinate is $coordinate."
            }
        }

        private const val MIN_COORDINATE_VALUE = 1
        private const val MAX_COORDINATE_VALUE = 10
        private val VALID_COORDINATE_RANGE = MIN_COORDINATE_VALUE..MAX_COORDINATE_VALUE
    }
}
