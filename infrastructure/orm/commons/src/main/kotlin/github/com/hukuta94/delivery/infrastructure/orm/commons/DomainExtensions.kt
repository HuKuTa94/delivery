package github.com.hukuta94.delivery.infrastructure.orm.commons

import arrow.core.getOrElse
import github.com.hukuta94.delivery.core.application.query.common.LocationResponse
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierName
import github.com.hukuta94.delivery.core.domain.common.Location

private const val LOCATION_COORDINATES_SEPARATOR = ','

/**
 * Convert [Location] value into database format.
 *
 * @return a [String] representing the coordinates, separated by a comma.
 */
fun Location.toDb() = "${this.x},${this.y}"

/**
 * Restores a [Location] value object from a database row.
 *
 * @return the reconstructed [Location] object.
 */
fun String.toLocation(): Location {
    return this.splitCoordinates().let { coordinate ->
        Location.of(
            x = coordinate[0].toInt(),
            y = coordinate[1].toInt(),
        ).getOrElse { error(it.message) }
    }
}

fun String.toLocationResponse(): LocationResponse {
    return this.splitCoordinates().let { coordinate ->
        LocationResponse(
            x = coordinate[0].toInt(),
            y = coordinate[1].toInt(),
        )
    }
}

private fun String.splitCoordinates() = this.split(LOCATION_COORDINATES_SEPARATOR)

/**
 * Convert [CourierName] value into database format.
 *
 * @return a [String] representing the coordinates, separated by a comma.
 */
fun CourierName.toDb() = this.value

/**
 * Restores a [CourierName] value object from a database row.
 *
 * @return the reconstructed [CourierName] object.
 */
fun String.toCourierName(): CourierName {
    return CourierName.of(this).getOrElse { error(it.message) }
}