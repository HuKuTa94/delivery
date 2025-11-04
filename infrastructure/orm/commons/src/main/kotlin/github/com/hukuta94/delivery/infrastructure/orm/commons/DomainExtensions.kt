package github.com.hukuta94.delivery.infrastructure.orm.commons

import arrow.core.getOrElse
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierName
import github.com.hukuta94.delivery.core.domain.common.Location

/**
 * Convert [Location] value into database format.
 *
 * @return a [String] representing the coordinates, separated by a comma.
 */
fun Location.toDb() = "${this.x},${this.y}"

/**
 * Restores a [Location] value object from a database row.
 *
 * @param row a [String] containing the coordinates separated by a comma.
 * @return the reconstructed [Location] object.
 */
fun Location.Companion.fromDb(row: String): Location {
    return row.split(',').let { coordinate ->
        Location.of(
            x = coordinate[0].toInt(),
            y = coordinate[1].toInt(),
        ).getOrElse { error(it.message) }
    }
}

/**
 * Convert [CourierName] value into database format.
 *
 * @return a [String] representing the coordinates, separated by a comma.
 */
fun CourierName.toDb() = this.value

/**
 * Restores a [CourierName] value object from a database row.
 *
 * @param row a [String] containing the name a courier.
 * @return the reconstructed [CourierName] object.
 */
fun CourierName.Companion.fromDb(row: String): CourierName {
    return CourierName.of(row).getOrElse { error(it.message) }
}