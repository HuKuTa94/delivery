package github.com.hukuta94.delivery.core.domain.sharedkernel

data class Location(
    val abscissa: Int,
    val ordinate: Int,
) {

    init {
        validate(abscissa)
        validate(ordinate)
    }

    /**
     * Должна быть возможность рассчитать расстояние между двумя Location.
     * Расстояние между Location - это совокупное количество шагов по X и Y,
     * которое необходимо сделать курьеру, чтобы достигнуть точки.
     */
    fun distanceTo(other: Location): Distance {
        return Distance(
            from = this,
            to = other,
        )
    }

    companion object {
        /**
         * Должна быть возможность создать рандомную координату.
         */
        fun random() = Location(
            abscissa = (MIN_COORDINATE_VALUE .. MAX_COORDINATE_VALUE).random(),
            ordinate = (MIN_COORDINATE_VALUE .. MAX_COORDINATE_VALUE).random(),
        )

        fun minimal() = Location(MIN_COORDINATE_VALUE, MIN_COORDINATE_VALUE)

        fun maximal() = Location(MAX_COORDINATE_VALUE, MAX_COORDINATE_VALUE)

        private fun validate(coordinate: Int) {
            if (coordinate < MIN_COORDINATE_VALUE || coordinate > MAX_COORDINATE_VALUE) {
                throw IllegalArgumentException(
                    "Expected coordinate must be between $MIN_COORDINATE_VALUE and $MAX_COORDINATE_VALUE. Actual coordinate is $coordinate."
                )
            }
        }

        private const val MIN_COORDINATE_VALUE = 1
        private const val MAX_COORDINATE_VALUE = 10
    }
}
