package github.com.hukuta94.delivery.core.domain.common

import arrow.core.raise.either
import github.com.hukuta94.delivery.core.domain.BusinessError
import github.com.hukuta94.delivery.core.domain.ValueObject
import kotlin.math.abs

data class Location private constructor(
    val x: Int,
    val y: Int,
) : ValueObject {

    fun distanceTo(other: Location): Int {
        val xDelta = this.x - other.x
        val yDelta = this.y - other.y

        return abs(xDelta) + abs(yDelta)
    }

    //TODO вынести общую логику в модуль orm:commons
    fun stringValue() = "$x,$y"

    companion object {

        fun of(x: Int, y: Int) = either {
            if (x !in VALID_COORDINATE_RANGE || y !in VALID_COORDINATE_RANGE) {
                raise(Error.CoordinatesOutOfRange(x, y))
            }
            Location(x, y)
        }

        fun random() = Location(
            x = (VALID_COORDINATE_RANGE).random(),
            y = (VALID_COORDINATE_RANGE).random(),
        )

        private val VALID_COORDINATE_RANGE = 1..10
    }

    sealed class Error(
        override val message: String,
    ) : BusinessError {
        data class CoordinatesOutOfRange internal constructor(
            val x: Int,
            val y: Int,
        ) : Error("Location coordinates must be in range $VALID_COORDINATE_RANGE. Actual coordinates are x=$x; y=$y")
    }
}
