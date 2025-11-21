package github.com.hukuta94.delivery.core.domain.common

import github.com.hukuta94.delivery.core.domain.LocationSpecification.DISTANCE_BETWEEN_TWO_SAME_LOCATIONS
import github.com.hukuta94.delivery.core.domain.LocationSpecification.MAX_COORDINATE_VALUE
import github.com.hukuta94.delivery.core.domain.LocationSpecification.MIN_COORDINATE_VALUE
import github.com.hukuta94.delivery.core.domain.LocationSpecification.VALID_COORDINATE_RANGE
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.math.abs

class LocationTest : StringSpec({

    "location is created when coordinates are within allowed range" {
        // Given
        val validCoordinates = Arb.int(VALID_COORDINATE_RANGE)

        checkAll(
            validCoordinates,
            validCoordinates,
        ) { x, y ->
            // When
            val result = Location.of(x, y).shouldBeRight()

            // Then
            assertSoftly {
                result.x shouldBe x
                result.y shouldBe y
            }
        }
    }

    "must be error when coordinate is out of range" {
        // Given
        val negativeOutOfRange = MIN_COORDINATE_VALUE - 1
        val positiveOutOfRange = MAX_COORDINATE_VALUE + 1
        val wrongCoordinateRanges = listOf(
            Pair(negativeOutOfRange, MIN_COORDINATE_VALUE),
            Pair(positiveOutOfRange, MIN_COORDINATE_VALUE),
            Pair(MIN_COORDINATE_VALUE, negativeOutOfRange),
            Pair(MIN_COORDINATE_VALUE, positiveOutOfRange),
            Pair(negativeOutOfRange, negativeOutOfRange),
            Pair(positiveOutOfRange, positiveOutOfRange),
        )

        checkAll(
            iterations = wrongCoordinateRanges.size,
            Arb.element(wrongCoordinateRanges),
        ) { (x, y) ->
            // When
            val result = Location.of(x, y)

            // Then
            result.shouldBeLeft().also {
                it::class shouldBe Location.Error.CoordinatesOutOfRange::class
                it.message shouldBe
                        "Location coordinates must be in range $VALID_COORDINATE_RANGE. " +
                        "Actual coordinates are x=$x; y=$y"
            }
        }
    }

    "random is always within allowed range" {
        repeat(100) {
            // When
            val result = Location.random()

            // Then
            assertSoftly {
                result.x shouldBeInRange VALID_COORDINATE_RANGE
                result.y shouldBeInRange VALID_COORDINATE_RANGE
            }
        }
    }

    "locations are equal when coordinates are the same" {
        // Given
        val x = MIN_COORDINATE_VALUE
        val y = MAX_COORDINATE_VALUE

        // When
        val locationA = Location.of(x, y)
        val locationB = Location.of(x, y)

        // Then
        locationA shouldBe locationB
    }

    "locations are not equal when coordinates differ" {
        // When
        val locationA = Location.of(MIN_COORDINATE_VALUE, MAX_COORDINATE_VALUE)
        val locationB = Location.of(MAX_COORDINATE_VALUE, MIN_COORDINATE_VALUE)

        // Then
        locationA shouldNotBe locationB
    }

    "distance between same locations is zero" {
        // Given
        val location = location(5, 5)

        // When
        val result = location.distanceTo(location)

        // Then
        result shouldBe DISTANCE_BETWEEN_TWO_SAME_LOCATIONS
    }

    "distance is symmetric" {
        // Given
        val locationA = location(1, 2)
        val locationB = location(7, 9)

        // When
        val resultAB = locationA.distanceTo(locationB)
        val resultBA = locationB.distanceTo(locationA)

        // Then
        resultAB shouldBe resultBA
    }

    "distance is valid when only X changes" {
        // Given
        val from = location(1, 1)
        val to = location(5, 1)

        // When
        val result = from.distanceTo(to)

        // Then
        result shouldBe 4
    }

    "distance is valid when only Y changes" {
        // Given
        val from = location(1, 1)
        val to = location(1, 5)

        // When
        val result = from.distanceTo(to)

        // Then
        result shouldBe 4
    }

    "distance must equal manhattan formula" {
        val validCoordinates = Arb.int(MIN_COORDINATE_VALUE, MAX_COORDINATE_VALUE)

        checkAll(
            validCoordinates,
            validCoordinates,
            validCoordinates,
            validCoordinates,
        ) { x1, y1, x2, y2 ->
            // Given
            val from = location(x1, y1)
            val to = location(x2, y2)

            // When
            val result = from.distanceTo(to)

            // Then
            val expected = abs(x1 - x2) + abs(y1 - y2)

            result shouldBe expected
        }
    }

    "distance must always be non-negative" {
        val validCoordinates = Arb.int(MIN_COORDINATE_VALUE, MAX_COORDINATE_VALUE)

        checkAll(
            validCoordinates,
            validCoordinates,
            validCoordinates,
            validCoordinates,
        ) { x1, y1, x2, y2 ->
            // Given
            val from = location(x1, y1)
            val to = location(x2, y2)

            // When
            val result = from.distanceTo(to)

            // Then
            result shouldBeGreaterThanOrEqual 0
        }
    }

    "distance with large coordinates still is calculated" {
        // Given
        val from = minLocation()
        val to = maxLocation()

        // When
        val result = from.distanceTo(to)

        // Then
        val expected = abs(from.x - to.x) + abs(from.y - to.y)

        result shouldBe expected
    }
})
