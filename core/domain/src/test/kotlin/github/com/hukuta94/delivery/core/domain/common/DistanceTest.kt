package github.com.hukuta94.delivery.core.domain.common

import github.com.hukuta94.delivery.core.domain.DistanceSpecification.MIN_DISTANCE_VALUE
import github.com.hukuta94.delivery.core.domain.LocationSpecification.MAX_COORDINATE_VALUE
import github.com.hukuta94.delivery.core.domain.LocationSpecification.MIN_COORDINATE_VALUE
import io.kotest.matchers.shouldBe
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.math.abs

class DistanceTest : StringSpec({

    "distance between same locations is zero" {
        // Given
        val location = newLocation(5, 5)

        // When
        val sut = Distance(location, location)

        // Then
        sut.value shouldBe MIN_DISTANCE_VALUE
    }

    "distance is symmetric" {
        // Given
        val locationA = newLocation(1, 2)
        val locationB = newLocation(7, 9)

        // When
        val sutAB = Distance(locationA, locationB)
        val sutBA = Distance(locationB, locationA)

        // Then
        sutAB.value shouldBe sutBA.value
    }

    "distance is valid when only abscissa changes" {
        // Given
        val from = newLocation(1, 1)
        val to = newLocation(5, 1)

        // When
        val sut = Distance(from, to)

        // Then
        sut.value shouldBe 4
    }

    "distance is valid when only ordinate changes" {
        // Given
        val from = newLocation(1, 1)
        val to = newLocation(1, 5)

        // When
        val sut = Distance(from, to)

        // Then
        sut.value shouldBe 4
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
            val sut = Distance(from, to)

            // Then
            val expected = abs(x1 - x2) + abs(y1 - y2)

            sut.value shouldBe expected
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
            val sut = Distance(from, to)

            // Then
            sut.value shouldBeGreaterThanOrEqual 0
        }
    }

    "distance with large coordinates still is calculated" {
        // Given
        val from = newLocationWithMinCoords()
        val to = newLocationWithMaxCoords()

        // When
        val sut = Distance(from, to)

        // Then
        val expected = abs(from.x - to.x) + abs(from.y - to.y)

        sut.value shouldBe expected
    }
})