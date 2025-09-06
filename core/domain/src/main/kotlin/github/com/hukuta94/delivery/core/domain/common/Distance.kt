package github.com.hukuta94.delivery.core.domain.common

import github.com.hukuta94.delivery.core.domain.ValueObject
import kotlin.math.abs

data class Distance(
    val from: Location,
    val to: Location,
) : ValueObject {
    fun abs(): Int {
        val absAbscissaDelta = absBetweenAbscissa()
        val absOrdinateDelta = absBetweenOrdinate()

        return absAbscissaDelta + absOrdinateDelta
    }

    fun absBetweenAbscissa() = abs(from.abscissa - to.abscissa)
    fun absBetweenOrdinate() = abs(from.ordinate - to.ordinate)
}
