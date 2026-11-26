package github.com.hukuta94.delivery.infrastructure.persistence.inmemory

import arrow.core.right
import github.com.hukuta94.delivery.core.application.port.GetLocationPort
import github.com.hukuta94.delivery.core.domain.common.Location

/**
 * Stub for [GetLocationPort] while the Geo gRPC service is unavailable:
 * ignores the street and always answers with a valid random [Location].
 */
class InMemoryGetLocation : GetLocationPort {
    override fun fromStreet(street: String) = Location.random().right()
}
