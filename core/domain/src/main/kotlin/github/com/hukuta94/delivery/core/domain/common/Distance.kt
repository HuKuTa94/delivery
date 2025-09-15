package github.com.hukuta94.delivery.core.domain.common

import github.com.hukuta94.delivery.core.domain.ValueObject
import kotlin.math.abs

@JvmInline
value class Distance private constructor(
    val value: Int,
) : ValueObject {

    companion object {
        /**
         * Calculates distance in absolute value between two [Location]s
         */
        operator fun invoke(
            from: Location,
            to: Location
        ): Distance {
            val xDelta = from.x - to.x
            val yDelta = from.y - to.y

            val value = abs(xDelta) + abs(yDelta)

            return Distance(value)
        }
    }
}
