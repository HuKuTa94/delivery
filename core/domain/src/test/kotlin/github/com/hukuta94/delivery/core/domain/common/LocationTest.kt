package github.com.hukuta94.delivery.core.domain.common

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class LocationTest {

    @Test
    fun `location is created with correct coordinates`() {
        val x = MIN_COORDINATE_VALUE
        val y = MAX_COORDINATE_VALUE

        val location = newLocation(x, y)

        location.abscissa shouldBe x
        location.ordinate shouldBe y
    }

    @ParameterizedTest
    @MethodSource("invalidMinimalCoordinates")
    fun `invalid minimal value of coordinate is cause of error`(x: Int, y: Int) {
        `invalid value of coordinate is cause of error`(x, y)
    }

    @ParameterizedTest
    @MethodSource("invalidMaximalCoordinates")
    fun `invalid maximal value of coordinate is cause of error`(x: Int, y: Int) {
        `invalid value of coordinate is cause of error`(x, y)
    }

    private fun `invalid value of coordinate is cause of error`(x: Int, y: Int) {
        val invalidCoordinate = if (x !in MIN_COORDINATE_VALUE..MAX_COORDINATE_VALUE) x else y

        shouldThrow <IllegalArgumentException> { newLocation(x, y) }.message shouldBe
            "Expected coordinate must be between $MIN_COORDINATE_VALUE and $MAX_COORDINATE_VALUE. Actual coordinate is $invalidCoordinate."
    }

    @ParameterizedTest
    @MethodSource("equalCoordinates")
    fun `one location is equal to other location when their coordinates are equal`(
        oneLocation: Location,
        otherLocation: Location
    ) {
        oneLocation shouldBe otherLocation
    }

    @ParameterizedTest
    @MethodSource("notEqualCoordinates")
    fun `one location is not equal to other location when their coordinates are not equal`(
        oneLocation: Location,
        otherLocation: Location
    ) {
        oneLocation shouldNotBe otherLocation
    }

    @Test
    fun `one location computes distance to other location`() {
        val location1 = newLocation(4, 9)
        val location2 = newLocation(2, 6)

        assertSoftly {
            // distance between different locations
            location1 .distanceTo (location2) .abs() shouldBe 5
            location2 .distanceTo (location1) .abs() shouldBe 5

            // distance to same location
            location1 .distanceTo (location1) .abs() shouldBe 0
            location2 .distanceTo (location2) .abs() shouldBe 0
        }
    }

    companion object {

        @JvmStatic
        fun equalCoordinates(): Stream<Arguments> {
            return (MIN_COORDINATE_VALUE..MAX_COORDINATE_VALUE).map { coordinate ->
                Arguments.of(
                    newLocation(coordinate, coordinate),
                    newLocation(coordinate, coordinate)
                )
            }.stream()
        }

        @JvmStatic
        fun notEqualCoordinates(): Stream<Arguments> {
            val combinations = mutableListOf<Arguments>()
            val coordinates = arrayOf(1, 2)

            for (x1 in coordinates) {
                for (y1 in coordinates) {
                    for (x2 in coordinates) {
                        for (y2 in coordinates) {
                            if (x1 == x2 && y1 == y2) {
                                continue
                            }
                            combinations.add(
                                Arguments.of(
                                    newLocation(x1, y1),
                                    newLocation(x2, y2),
                                )
                            )
                        }
                    }
                }
            }

            return combinations.stream()
        }

        @JvmStatic
        fun invalidMinimalCoordinates(): Stream<Arguments> {
            return createInvalidCoordinateCombinations(
                validCoordinate = MIN_COORDINATE_VALUE,
                invalidCoordinate = MIN_COORDINATE_VALUE - 1
            )
        }

        @JvmStatic
        fun invalidMaximalCoordinates(): Stream<Arguments> {
            return createInvalidCoordinateCombinations(
                validCoordinate = MAX_COORDINATE_VALUE,
                invalidCoordinate = MAX_COORDINATE_VALUE + 1
            )
        }

        /**
         * Creates coordinate combinations with one valid and one invalid coordinate value.
         */
        private fun createInvalidCoordinateCombinations(
            validCoordinate: Int,
            invalidCoordinate: Int,
        ): Stream<Arguments> {
            val combinations = mutableListOf<Arguments>()
            val coordinates = arrayOf(validCoordinate, invalidCoordinate)

            for (x in coordinates) {
                for (y in coordinates) {
                    if (x == validCoordinate && y == validCoordinate) {
                        continue
                    }
                    combinations.add(Arguments.of(x, y))
                }
            }

            return combinations.stream()
        }

        private const val MIN_COORDINATE_VALUE = 1
        private const val MAX_COORDINATE_VALUE = 10
    }
}