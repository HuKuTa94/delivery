package github.com.hukuta94.delivery.core.domain.aggregate.courier

import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import github.com.hukuta94.delivery.core.domain.BusinessError

enum class Transport(
    val id: Int,
    val speed: Int,
) {
    PEDESTRIAN(id = 1, speed = 1),
    BICYCLE(id = 2, speed = 2),
    CAR(id = 3, speed = 3),
    DRONE(id = 4, speed = 4),
    ;

    sealed class Error(
        override val message: String,
    ) : BusinessError {
        data class UnknownTransport(
            val code: String,
        ) : Error(
            message = "Transport \"$code\" is unknown. Known transports are ${Transport.entries.joinToString()}",
        )
    }

    companion object {
        fun from(id: Int): Transport = entries
            .firstOrNull { it.id == id }
            ?: throw IllegalArgumentException("Enum value is not found by id: $id")

        /**
         * Finds the transport by its code, the exact name of the transport, e.g. `BICYCLE`.
         */
        fun of(code: String) = either {
            ensureNotNull(entries.firstOrNull { it.name == code }) {
                Error.UnknownTransport(code)
            }
        }
    }
}
