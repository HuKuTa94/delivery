package github.com.hukuta94.delivery.core.domain.common

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class DistanceTest {

    @Test
    fun `abs - returns correct distance`() {
        // Given
        val from = newLocation(1, 1)
        val to = newLocation(3, 4)
        val distance = Distance(from, to)

        // When
        val result = distance.abs()

        // Then
        val expectedDistance = 5 // |3-1| + |4-1|
        result shouldBe expectedDistance
    }

    @Test
    fun `abs - returns 0 for the same locations`() {
        // Given
        val from = newLocation(5, 5)
        val to = newLocation(5, 5)
        val distance = Distance(from, to)

        // When
        val result = distance.abs()

        // Then
        result shouldBe 0 // |5-5| + |5-5|
    }

    @Test
    fun `absBetweenAbscissa - returns absolute difference between abscissas`() {
        // Given
        val from = newLocation(5, 5)
        val to = newLocation(10, 5)
        val distance = Distance(from, to)

        // When
        val result = distance.absBetweenAbscissa()

        // Then
        result shouldBe 5
    }

    @Test
    fun `absBetweenAbscissa - returns 0 for the same abscissas`() {
        // Given
        val from = newLocation(5, 5)
        val to = newLocation(5, 10)
        val distance = Distance(from, to)

        // When
        val result = distance.absBetweenAbscissa()

        // Then
        result shouldBe 0
    }

    @Test
    fun `absBetweenOrdinate - returns absolute difference between ordinates`() {
        // Given
        val from = newLocation(5, 5)
        val to = newLocation(5, 10)
        val distance = Distance(from, to)

        // When
        val result = distance.absBetweenOrdinate()

        // Then
        result shouldBe 5
    }

    @Test
    fun `absBetweenOrdinate - returns 0 for the same ordinates`() {
        // Given
        val from = newLocation(5, 5)
        val to = newLocation(10, 5)
        val distance = Distance(from, to)

        // When
        val result = distance.absBetweenOrdinate()

        // Then
        result shouldBe 0
    }

    @Test
    fun `abs - handles negative coordinates correctly`() {
        // Given
        val from = newLocation(1, 1)
        val to = newLocation(10, 10)
        val distance = Distance(from, to)

        // When
        val result = distance.abs()

        // Then
        result shouldBe 18 // |1-10| + |1-10|
    }
}