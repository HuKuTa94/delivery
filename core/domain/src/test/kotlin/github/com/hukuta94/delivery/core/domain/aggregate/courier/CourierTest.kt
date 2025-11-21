package github.com.hukuta94.delivery.core.domain.aggregate.courier

import github.com.hukuta94.delivery.core.domain.LocationSpecification.MAX_COORDINATE_VALUE
import github.com.hukuta94.delivery.core.domain.LocationSpecification.VALID_COORDINATE_RANGE
import github.com.hukuta94.delivery.core.domain.common.Location
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.core.domain.common.minLocation
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.of
import io.kotest.property.checkAll
import kotlin.math.abs

internal class CourierTest : StringSpec ({

    "new courier is created in valid state and status" {
        checkAll(TRANSPORTS) { transport ->
            // Given
            val name = courierName()
            val location = minLocation()

            // When
            val courier = Courier.create(
                name = name,
                transport = transport,
                location = location,
            )

            // Then
            assertSoftly {
                courier.name shouldBe name
                courier.transport shouldBe transport
                courier.location shouldBe location
                courier.status shouldBe CourierStatus.FREE
            }
        }
    }

    "courier must become free" {
        // Given
        val courier = freeCourier()

        // When
        courier.free()

        // Then
        courier.status shouldBe CourierStatus.FREE
    }

    "courier must become busy" {
        // Given
        val sut = freeCourier()

        // When
        sut.busy()

        // Then
        sut.status shouldBe CourierStatus.BUSY
    }

    "courier must calculate time to other location by using his transport" {
        val validLocations = Arb.bind(
            Arb.int(VALID_COORDINATE_RANGE),
            Arb.int(VALID_COORDINATE_RANGE)
        ) { x, y -> location(x, y) }

        checkAll(
            validLocations,
            validLocations,
            TRANSPORTS
        ) { courierLocation, otherLocation, transport ->
            // Given
            val sut = freeCourier(
                location = courierLocation,
                transport = transport,
            )

            // When
            val actual = sut.timeToLocation(otherLocation)

            // Then
            val distance = courierLocation.distanceTo(otherLocation)
            val expected = distance / transport.speed.toDouble()
            actual shouldBe expected
        }
    }

    "when target location is front of courier then he must move forward" {
        checkAll(TRANSPORTS) { transport ->
            // Given
            val sut = freeCourier(transport = transport, location = COURIER_START_LOCATION)
            val forward = CENTER + transport.speed
            val target = location(CENTER, forward)
            val expected = location(CENTER, forward)

            // When
            sut.moveTo(target)

            // Then
            sut.location shouldBe expected
        }
    }

    "when target location is front and right of courier then he must move forward" {
        checkAll(TRANSPORTS) { transport ->
            // Given
            val sut = freeCourier(transport = transport, location = COURIER_START_LOCATION)
            val forward = CENTER + transport.speed
            val right = CENTER + transport.speed
            val target = location(right, forward)
            val expected = location(CENTER, forward)

            // When
            sut.moveTo(target)

            // Then
            sut.location shouldBe expected
        }
    }

    "when target location is right of courier then he must move right" {
        checkAll(TRANSPORTS) { transport ->
            // Given
            val sut = freeCourier(transport = transport, location = COURIER_START_LOCATION)
            val right = CENTER + transport.speed
            val target = location(right, CENTER)
            val expected = location(right, CENTER)

            // When
            sut.moveTo(target)

            // Then
            sut.location shouldBe expected
        }
    }

    "when target location is right and backward of courier then he must move backward" {
        checkAll(TRANSPORTS) { transport ->
            // Given
            val sut = freeCourier(transport = transport, location = COURIER_START_LOCATION)
            val right = CENTER + transport.speed
            val backward = CENTER - transport.speed
            val target = location(right, backward)
            val expected = location(CENTER, backward)

            // When
            sut.moveTo(target)

            // Then
            sut.location shouldBe expected
        }
    }

    "when target location is backward of courier then he must move backward" {
        checkAll(TRANSPORTS) { transport ->
            // Given
            val sut = freeCourier(transport = transport, location = COURIER_START_LOCATION)
            val backward = CENTER - transport.speed
            val target = location(CENTER, backward)
            val expected = location(CENTER, backward)

            // When
            sut.moveTo(target)

            // Then
            sut.location shouldBe expected
        }
    }

    "when target location is left and backward of courier then he must move backward" {
        checkAll(TRANSPORTS) { transport ->
            // Given
            val sut = freeCourier(transport = transport, location = COURIER_START_LOCATION)
            val left = CENTER - transport.speed
            val backward = CENTER - transport.speed
            val target = location(left, backward)
            val expected = location(CENTER, backward)

            // When
            sut.moveTo(target)

            // Then
            sut.location shouldBe expected
        }
    }

    "when target location is left of courier then he must move left" {
        checkAll(TRANSPORTS) { transport ->
            // Given
            val sut = freeCourier(transport = transport, location = COURIER_START_LOCATION)
            val left = CENTER - transport.speed
            val target = location(left, CENTER)
            val expected = location(left, CENTER)

            // When
            sut.moveTo(target)

            // Then
            sut.location shouldBe expected
        }
    }

    "when target location is left and forward of courier then he must move forward" {
        checkAll(TRANSPORTS) { transport ->
            // Given
            val sut = freeCourier(transport = transport, location = COURIER_START_LOCATION)
            val left = CENTER - transport.speed
            val forward = CENTER + transport.speed
            val target = location(left, forward)
            val expected = location(CENTER, forward)

            // When
            sut.moveTo(target)

            // Then
            sut.location shouldBe expected
        }
    }

    "courier can reach every reachable target location in one step according to transport speed" {
        /**
         * Computes all test cases of closest target locations to courier
         * (not further than courier can reach for one movement)
         */
        fun computeAllTargetLocationsFor(transport: Transport): List<Location> {
            val result = mutableListOf<Location>()

            // take reachable range of coordinates for one movement based on transport speed
            for (deltaX in -transport.speed..transport.speed) {
                val deltaYAbs = transport.speed - abs(deltaX)
                result.add(
                    location(
                        x = COURIER_START_LOCATION.x + deltaX,
                        y = COURIER_START_LOCATION.y + deltaYAbs
                    )
                )

                // don't duplicate coordinate
                if (deltaYAbs != 0) {
                    result.add(
                        location(
                            x = COURIER_START_LOCATION.x + deltaX,
                            y = COURIER_START_LOCATION.y - deltaYAbs
                        )
                    )
                }
            }
            return result
        }

        Transport.entries.forEach { transport ->
            val targets = computeAllTargetLocationsFor(transport)

            checkAll(
                iterations = targets.size, // execute only precomputed test cases
                Arb.of(targets),
            ) { target ->
                // Given
                val sut = freeCourier(
                    transport = transport,
                    location = COURIER_START_LOCATION,
                )

                // When
                sut.moveTo(target)

                // Then
                withClue(
                    "Courier with transport = $transport must reach target $target from start $COURIER_START_LOCATION, actual is ${sut.location}"
                ) {
                    sut.location shouldBe target
                }
            }
        }
    }
}) {
    companion object {
        private val TRANSPORTS = Arb.element(Transport.entries)
        private val COURIER_START_LOCATION = location(CENTER, CENTER)
        private const val CENTER = (MAX_COORDINATE_VALUE / 2)
    }
}