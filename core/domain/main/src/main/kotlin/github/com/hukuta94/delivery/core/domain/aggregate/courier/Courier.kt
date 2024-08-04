package github.com.hukuta94.delivery.core.domain.aggregate.courier

import github.com.hukuta94.delivery.core.domain.aggregate.Aggregate
import github.com.hukuta94.delivery.core.domain.common.Location
import java.util.UUID

class Courier internal constructor(
    override val id: UUID,
    val name: CourierName,
    val transport: Transport,
    var location: Location,
) : Aggregate<UUID>() {
    lateinit var status: CourierStatus private set

    fun busy() {
        status = CourierStatus.BUSY
    }

    fun free() {
        status = CourierStatus.FREE
    }

    fun timeToLocation(location: Location): Double {
        val distance = this.location
            .distanceTo(location)
            .abs()

        return distance.toDouble() / transport.speed
    }

    /**
     * The courier moves one step to another [Location] clockwise: up - right - down - left
     */
    fun moveTo(location: Location) {
        val abscissaDifference = location.abscissa - this.location.abscissa
        val ordinateDifference = location.ordinate - this.location.ordinate

        val isSameLocation = abscissaDifference == 0 && ordinateDifference == 0
        if (isSameLocation) {
            return
        }

        val distanceToLocation = this.location.distanceTo(location)
        val courierMovementAbscissaStep = minOf(distanceToLocation.absBetweenAbscissa(), transport.speed)
        val courierMovementOrdinateStep = minOf(distanceToLocation.absBetweenOrdinate(), transport.speed)

        val newAbscissa = when {
            abscissaDifference < 0 -> this.location.abscissa - courierMovementAbscissaStep // Moves up
            abscissaDifference > 0 -> this.location.abscissa + courierMovementAbscissaStep // Moves down
            else -> this.location.abscissa
        }

        val newOrdinate = when {
            ordinateDifference < 0 -> this.location.ordinate - courierMovementOrdinateStep // Moves left
            ordinateDifference > 0 -> this.location.ordinate + courierMovementOrdinateStep // Moves right
            else -> this.location.ordinate
        }

        this.location = Location(newAbscissa, newOrdinate)
    }

    companion object {
        fun create(
            name: CourierName,
            transport: Transport,
            location: Location,
        ): Courier {
            return Courier(
                id = UUID.randomUUID(),
                name = name,
                location = location,
                transport = transport
            ).apply {
                status = CourierStatus.FREE
            }
        }
    }
}