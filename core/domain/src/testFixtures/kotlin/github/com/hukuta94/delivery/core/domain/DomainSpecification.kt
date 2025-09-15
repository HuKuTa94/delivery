package github.com.hukuta94.delivery.core.domain

/**
 * Location represents current coordinates of the courier and the order in defined range
 * where couriers can move and deliver their orders.
 * Must be ability to create location with random coordinates in allowed range.
 */
internal object LocationSpecification {
    const val MIN_COORDINATE_VALUE = 1
    const val MAX_COORDINATE_VALUE = 10
    val VALID_COORDINATE_RANGE = (MIN_COORDINATE_VALUE..MAX_COORDINATE_VALUE)
}

/**
 * It must be possible to calculate the distance between two Locations.
 * The distance between two locations is the cumulative number of X and Y steps
 * that the courier needs to take to reach the point.
 *
 * If the distance is zero it means that two locations are same and have same coordinates.
 */
internal object DistanceSpecification {
    const val MIN_DISTANCE_VALUE = 0
}

 /**
 * Courier name represents a string that are not blank or empty with length in allowed range.
 * The characters in courier name can be cyrillic and latin
 * Not empty courier name must be trimmed (does not have empty spaces at begin and end of the string)
 */
internal object CourierNameSpecification {
    const val MIN_COURIER_NAME_LENGTH = 2
    const val MAX_COURIER_NAME_LENGTH = 30
}