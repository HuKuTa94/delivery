package github.com.hukuta94.delivery.core.domain.aggregate.courier

import github.com.hukuta94.delivery.core.domain.common.*
import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

internal class CourierTest {

    @Test
    fun `new courier is created in valid status`() {
        val name = courierName()
        val transport = bicycle()
        val location = randomLocation()

        val courier = Courier.create(
            name = name,
            transport = transport,
            location = location,
        )

        assertSoftly {
            courier.name shouldBe name
            courier.transport shouldBe transport
            courier.location shouldBe location
            courier.status shouldBe CourierStatus.FREE
        }
    }

    @Test
    fun `the courier can become free`() {
        val courier = newCourier()

        courier.free()

        courier.status shouldBe CourierStatus.FREE
    }

    @Test
    fun `the courier can become busy`() {
        val courier = newCourier()

        courier.busy()

        courier.status shouldBe CourierStatus.BUSY
    }

    @ParameterizedTest
    @MethodSource("allCourierTransportsWithTimeToAnotherLocation")
    fun `the courier can calculate time to another location based on his transport speed`(
        startLocation: Location,
        endLocation: Location,
        transport: Transport,
        expectedTime: Double,
    ) {
        val courier = newCourier(
            transport = transport,
            location = startLocation,
        )

        val timeToLocation = courier.timeToLocation(endLocation)

        timeToLocation shouldBe expectedTime
    }

    @ParameterizedTest
    @MethodSource("courierMovesToOrderLocationsOnEachTransport")
    fun `the courier moves to order location with the step based on his transport speed`(
        courier: Courier,
        orderLocation: Location,
        courierLocationAfterMovement: Location,
    ) {
        assertCourierLocationAfterMoveToOrder(courier, orderLocation, courierLocationAfterMovement)
    }

    @ParameterizedTest
    @MethodSource("courierReachesOrderLocationsOnEachTransport")
    fun `the courier reaches exactly in the order location on his transport`(
        courier: Courier,
        orderLocation: Location,
        courierLocationAfterMovement: Location,
    ) {
        assertCourierLocationAfterMoveToOrder(courier, orderLocation, courierLocationAfterMovement)
    }

    private fun assertCourierLocationAfterMoveToOrder(
        courier: Courier,
        orderLocation: Location,
        courierLocationAfterMovement: Location
    ) {
        courier.moveTo(orderLocation)

        courier.location shouldBe courierLocationAfterMovement
    }

    companion object {
        @JvmStatic
        fun allCourierTransportsWithTimeToAnotherLocation(): Stream<Arguments> {
            val startLocation = minimalLocation()
            val endLocation = maximalLocation()

            return Transport.values().map { transport ->
                val expectedTime = startLocation.distanceTo(endLocation).abs() / transport.speed.toDouble()

                Arguments.of(
                    startLocation,
                    endLocation,
                    transport,
                    expectedTime,
                )
            }.stream()
        }

        @JvmStatic
        fun courierMovesToOrderLocationsOnEachTransport(): Stream<Arguments> {
            val maxTransportSpeed = Transport.values().maxOf { it.speed }
            val orderLocations = allPossibleLocationsWithShiftsFromCourier(
                shift = maxTransportSpeed, // shift by max possible value of transport speed
            )

            return Transport.values().flatMap { transport ->
                val expectedCourierLocationsAfterMovement = allPossibleLocationsWithShiftsFromCourier(
                    shift = transport.speed,
                )
                orderAndExpectedCourierLocationsForTransport(
                    transport = transport,
                    orderLocations = orderLocations,
                    expectedCourierLocations = expectedCourierLocationsAfterMovement,
                )
            }.stream()
        }

        @JvmStatic
        fun courierReachesOrderLocationsOnEachTransport(): Stream<Arguments> {
            val orderLocations = allPossibleLocationsWithShiftsFromCourier(shift = 1)

            return Transport.values().flatMap { transport ->
                orderAndExpectedCourierLocationsForTransport(
                    transport = transport,
                    orderLocations = orderLocations,
                    expectedCourierLocations = orderLocations, // expected courier locations are same as order locations
                )
            }.stream()
        }

        private fun allPossibleLocationsWithShiftsFromCourier(shift: Int): List<Location> {
            val shifts = listOf(
                0, // same location
                shift,
                -shift
            )

            return shifts.flatMap { xShift ->
                shifts.map { yShift ->
                    newLocation(
                        abscissa = COURIER_START_LOCATION.abscissa + xShift,
                        ordinate = COURIER_START_LOCATION.ordinate + yShift
                    )
                }
            }
        }

        private fun orderAndExpectedCourierLocationsForTransport(
            transport: Transport,
            orderLocations: List<Location>,
            expectedCourierLocations: List<Location>,
        ): List<Arguments> {
            return orderLocations.mapIndexed { i, orderLocation ->
                Arguments.of(
                    newCourier(
                        transport = transport,
                        location = COURIER_START_LOCATION,
                    ),
                    orderLocation,
                    expectedCourierLocations[i],
                )
            }
        }

        private val COURIER_START_LOCATION = newLocation(5, 5)
    }
}