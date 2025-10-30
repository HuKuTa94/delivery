package github.com.hukuta94.delivery.core.domain.common

import github.com.hukuta94.delivery.core.domain.DistanceSpecification.MIN_DISTANCE_VALUE
import github.com.hukuta94.delivery.core.domain.LocationSpecification.VALID_COORDINATE_RANGE
import github.com.hukuta94.delivery.core.domain.LocationSpecification.MAX_COORDINATE_VALUE
import github.com.hukuta94.delivery.core.domain.LocationSpecification.MIN_COORDINATE_VALUE
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.property.arbitrary.int
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
            val sut = Location(x, y)

            // Then
            assertSoftly {
                sut.x shouldBe x
                sut.y shouldBe y
            }
        }
    }

    "must throw exception when abscissa is out of range" {
        assertSoftly {
            shouldThrow<IllegalArgumentException> {
                Location(MIN_COORDINATE_VALUE - 1, MIN_COORDINATE_VALUE)
            }
            shouldThrow<IllegalArgumentException> {
                Location(MAX_COORDINATE_VALUE + 1, MIN_COORDINATE_VALUE)
            }
        }
    }

    "must throw exception when ordinate is out of range" {
        assertSoftly {
            shouldThrow<IllegalArgumentException> {
                Location(MIN_COORDINATE_VALUE, MIN_COORDINATE_VALUE - 1)
            }
            shouldThrow<IllegalArgumentException> {
                Location(MIN_COORDINATE_VALUE, MAX_COORDINATE_VALUE + 1)
            }
        }
    }

    "random is always within allowed range" {
        repeat(100) {
            // When
            val sut = Location.random()

            // Then
            assertSoftly {
                sut.x shouldBeInRange VALID_COORDINATE_RANGE
                sut.y shouldBeInRange VALID_COORDINATE_RANGE
            }
        }
    }

    "locations are equal when coordinates are the same" {
        // Given
        val abscissa = MIN_COORDINATE_VALUE
        val ordinate = MAX_COORDINATE_VALUE

        // When
        val sutA = Location(abscissa, ordinate)
        val sutB = Location(abscissa, ordinate)

        // Then
        sutA shouldBe sutB
    }

    "locations are not equal when coordinates differ" {
        // When
        val sutA = Location(MIN_COORDINATE_VALUE, MAX_COORDINATE_VALUE)
        val sutB = Location(MAX_COORDINATE_VALUE, MIN_COORDINATE_VALUE)

        // Then
        sutA shouldNotBe sutB
    }

    "location must be constructible for all valid coordinates" {
        // Given
        val validCoordinates = Arb.int(VALID_COORDINATE_RANGE)

        checkAll(
            validCoordinates,
            validCoordinates,
        ) { x, y ->
            // When
            val sut = Location(x, y)

            // Then
            sut.x shouldBe x
            sut.y shouldBe y
        }
    }

    "distance between same locations is zero" {
        // Given
        val location = newLocation(5, 5)

        // When
        val sut = location.distanceTo(location)

        // Then
        sut shouldBe MIN_DISTANCE_VALUE
    }

    "distance is symmetric" {
        // Given
        val locationA = newLocation(1, 2)
        val locationB = newLocation(7, 9)

        // When
        val sutAB = locationA.distanceTo(locationB)
        val sutBA = locationB.distanceTo(locationA)

        // Then
        sutAB shouldBe sutBA
    }

    "distance is valid when only abscissa changes" {
        // Given
        val from = newLocation(1, 1)
        val to = newLocation(5, 1)

        // When
        val sut = from.distanceTo(to)

        // Then
        sut shouldBe 4
    }

    "distance is valid when only ordinate changes" {
        // Given
        val from = newLocation(1, 1)
        val to = newLocation(1, 5)

        // When
        val sut = from.distanceTo(to)

        // Then
        sut shouldBe 4
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
            val from = newLocation(x1, y1)
            val to = newLocation(x2, y2)

            // When
            val sut = from.distanceTo(to)

            // Then
            val expected = abs(x1 - x2) + abs(y1 - y2)

            sut shouldBe expected
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
            val from = newLocation(x1, y1)
            val to = newLocation(x2, y2)

            // When
            val sut = from.distanceTo(to)

            // Then
            sut shouldBeGreaterThanOrEqual 0
        }
    }

    "distance with large coordinates still is calculated" {
        // Given
        val from = newLocationWithMinCoords()
        val to = newLocationWithMaxCoords()

        // When
        val sut = from.distanceTo(to)

        // Then
        val expected = abs(from.x - to.x) + abs(from.y - to.y)

        sut shouldBe expected
    }
})