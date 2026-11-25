package github.com.hukuta94.delivery.core.application.port

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import github.com.hukuta94.delivery.core.domain.common.Location
import java.util.concurrent.ConcurrentHashMap

/**
 * Answers only for streets stubbed with [locate] or [failToLocate] and fails loudly for any other street,
 * so a test also notices when the wrong street is asked for.
 */
class GetLocationPortFake : GetLocationPort {

    private val results = ConcurrentHashMap<String, Either<Location.Error, Location>>()

    override fun fromStreet(street: String): Either<Location.Error, Location> {
        return results[street] ?: error("No location is stubbed for street=$street")
    }

    fun locate(
        street: String,
        location: Location,
    ) {
        results[street] = location.right()
    }

    fun failToLocate(
        street: String,
        error: Location.Error,
    ) {
        results[street] = error.left()
    }

    fun clear() {
        results.clear()
    }
}
