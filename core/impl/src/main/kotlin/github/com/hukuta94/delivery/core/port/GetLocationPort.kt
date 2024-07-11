package github.com.hukuta94.delivery.core.port

import github.com.hukuta94.delivery.core.domain.sharedkernel.Location

/**
 * Port of application layer to get [Location] value object by different ways.
 * For example from street [String]
 */
interface GetLocationPort {

    fun getFromStreet(street: String): Location
}