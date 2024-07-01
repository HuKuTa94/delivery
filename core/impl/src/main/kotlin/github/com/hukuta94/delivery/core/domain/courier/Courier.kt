package github.com.hukuta94.delivery.core.domain.courier

import github.com.hukuta94.delivery.core.domain.sharedkernel.Location
import java.util.UUID

class Courier internal constructor(
    val id: UUID,
    val name: CourierName,
    val transport: Transport,
    var location: Location,
) {
    lateinit var status: CourierStatus private set

    fun busy() {
        status = CourierStatus.BUSY
    }

    fun free() {
        status = CourierStatus.FREE
    }

    fun timeToLocation(location: Location): Double {
        val distance = this.location.distanceTo(location)
        return distance.toDouble() / transport.speed
    }

    /**
     * The courier moves one step to another [Location] clockwise: up - right - down - left
     */
    fun moveTo(location: Location) {
        val abscissaDifference = location.abscissa - this.location.abscissa
        val ordinateDifference = location.ordinate - this.location.ordinate

        val distanceToLocation = this.location.distanceTo(location)
        val courierMovementStep = minOf(distanceToLocation, transport.speed)

        val newAbscissa = when {
            abscissaDifference < 0 -> this.location.abscissa - courierMovementStep // Moves up
            abscissaDifference > 0 -> this.location.abscissa + courierMovementStep // Moves down
            else -> this.location.abscissa
        }

        val newOrdinate = when {
            ordinateDifference < 0 -> this.location.ordinate - courierMovementStep // Moves left
            ordinateDifference > 0 -> this.location.ordinate + courierMovementStep // Moves right
            else -> this.location.ordinate
        }

        val isSameLocation = this.location.abscissa == newAbscissa && this.location.ordinate == newOrdinate
        if (isSameLocation) {
            return
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