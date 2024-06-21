package github.com.hukuta94.delivery.core.domain.sharedkernel

import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class LocationTest {

    @Test
    fun `location object is created with correct coordinates`() {
        val x = MIN_COORDINATE_VALUE
        val y = MAX_COORDINATE_VALUE

        val location = Location(x, y)

        assertEquals(x, location.abscissa)
        assertEquals(y, location.ordinate)
    }

    @ParameterizedTest
    @MethodSource("invalidMinimalCoordinatesProvider")
    fun `location constructor throws error for invalid minimum coordinate value`(x: Int, y: Int) {
        val invalidCoordinate = if (x < MIN_COORDINATE_VALUE) x else y

        assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy { Location(x, y) }
            .withMessage(
                "Expected coordinate should be between $MIN_COORDINATE_VALUE and $MAX_COORDINATE_VALUE. Actual coordinate is $invalidCoordinate."
            )
    }

    @ParameterizedTest
    @MethodSource("invalidMaximalCoordinatesProvider")
    fun `location constructor throws error for invalid maximum coordinate value`(x: Int, y: Int) {
        val invalidCoordinate = if (x > MAX_COORDINATE_VALUE) x else y

        assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy { Location(x, y) }
            .withMessage(
                "Expected coordinate should be between $MIN_COORDINATE_VALUE and $MAX_COORDINATE_VALUE. Actual coordinate is $invalidCoordinate."
            )
    }

    @ParameterizedTest
    @MethodSource("equalCoordinatesProvider")
    fun `two coordinates are equal if their X and Y are equal`(location1: Location, location2: Location) {
        assertEquals(location1, location2)
    }

    @ParameterizedTest
    @MethodSource("notEqualCoordinatesProvider")
    fun `two coordinates are not equal if their X or Y are not equal`(location1: Location, location2: Location) {
        assertNotEquals(location1, location2)
    }

    @Test
    fun `distance to target location`() {
        val location1 = Location(4, 9)
        val location2 = Location(2, 6)

        SoftAssertions().run {
            assertThat( location1.distanceTo(location2) ).isEqualTo(5)
            assertThat( location2.distanceTo(location1) ).isEqualTo(5)

            assertThat( location1.distanceTo(location1) ).isEqualTo(0)
            assertThat( location2.distanceTo(location2) ).isEqualTo(0)

            assertAll()
        }
    }

    @Test
    fun `exists ability to create random coordinate`() {
        Location.random()
    }

    companion object {

        @JvmStatic
        fun equalCoordinatesProvider(): Stream<Arguments> {
            return (MIN_COORDINATE_VALUE..MAX_COORDINATE_VALUE).map { coordinate ->
                Arguments.of(
                    Location(coordinate, coordinate),
                    Location(coordinate, coordinate)
                )
            }.stream()
        }

        @JvmStatic
        fun notEqualCoordinatesProvider(): Stream<Arguments> {
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
                                    Location(x1, y1),
                                    Location(x2, y2),
                                )
                            )
                        }
                    }
                }
            }

            return combinations.stream()
        }

        @JvmStatic
        fun invalidMinimalCoordinatesProvider(): Stream<Arguments> {
            return invalidCoordinatesProvider(MIN_COORDINATE_VALUE, MIN_COORDINATE_VALUE - 1)
        }

        @JvmStatic
        fun invalidMaximalCoordinatesProvider(): Stream<Arguments> {
            return invalidCoordinatesProvider(MAX_COORDINATE_VALUE, MAX_COORDINATE_VALUE + 1)
        }

        private fun invalidCoordinatesProvider(
            coordinate1: Int,
            coordinate2: Int,
        ): Stream<Arguments> {
            val combinations = mutableListOf<Arguments>()
            val coordinates = arrayOf(coordinate1, coordinate2)

            for (x in coordinates) {
                for (y in coordinates) {
                    if (x == coordinate1 && y == coordinate1 ||
                        x == coordinate2 && y == coordinate2) {
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