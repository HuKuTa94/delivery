package github.com.hukuta94.delivery.core.domain.common

import github.com.hukuta94.delivery.core.domain.LocationSpecification.VALID_COORDINATE_RANGE
import github.com.hukuta94.delivery.core.domain.LocationSpecification.MAX_COORDINATE_VALUE
import github.com.hukuta94.delivery.core.domain.LocationSpecification.MIN_COORDINATE_VALUE
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.property.arbitrary.int

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
})