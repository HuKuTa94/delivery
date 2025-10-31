package github.com.hukuta94.delivery.core.domain.aggregate.courier

import arrow.core.getOrElse
import github.com.hukuta94.delivery.core.domain.aggregate.Aggregate
import github.com.hukuta94.delivery.core.domain.common.Location
import java.util.UUID
import kotlin.math.abs
import kotlin.math.min

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
        return this.location.distanceTo(location).toDouble() / transport.speed
    }

    /**
     * The courier moves one step to another [Location] clockwise: up - right - down - left
     */
    fun moveTo(location: Location) {
        val yDelta = this.location.y - location.y
        val xDelta = this.location.x - location.x

        val yStep = computeStep(yDelta, transport.speed)
        val xStep = computeStep(xDelta, transport.speed - abs(yStep))

        this.location = Location.of(
            x = this.location.x + xStep,
            y = this.location.y + yStep,
        ).getOrElse { error -> throw IllegalStateException("Invalid location after move: $error") }
    }

    private fun computeStep(
        coordinateDelta: Int,
        availableCourierStepCount: Int,
    ) = when {
        coordinateDelta > 0 -> -min( coordinateDelta, availableCourierStepCount)
        coordinateDelta < 0 ->  min(-coordinateDelta, availableCourierStepCount)
        else -> 0
    }

    companion object {
        fun create(
            name: CourierName,
            transport: Transport,
            location: Location,
            id: UUID = UUID.randomUUID(),
        ): Courier {
            return Courier(
                id = id,
                name = name,
                location = location,
                transport = transport
            ).apply {
                status = CourierStatus.FREE
            }
        }
    }
}