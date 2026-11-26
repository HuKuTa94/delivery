package github.com.hukuta94.delivery.core.application.usecase.courier

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.nel
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.core.raise.zipOrAccumulate
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.application.usecase.UseCase
import github.com.hukuta94.delivery.core.domain.BusinessError
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierName
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Transport
import github.com.hukuta94.delivery.core.domain.common.Location
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * Registers a new courier who is free to take orders at once.
 * Every invalid detail of the command is reported, not only the first one.
 */
class RegisterCourierUseCase(
    private val courierRepository: CourierRepositoryPort,
) : UseCase {

    /**
     * @return id of the registered courier
     */
    operator fun invoke(command: RegisterCourierCommand): Either<NonEmptyList<Error>, UUID> = either {
        val courier = zipOrAccumulate(
            { CourierName.of(command.name).mapLeft(Error::InvalidName).bind() },
            { Transport.of(command.transport).mapLeft(Error::InvalidTransport).bind() },
            { Location.of(x = command.location.x, y = command.location.y).mapLeft(Error::InvalidLocation).bind() },
        ) { name, transport, location ->
            Courier.create(
                name = name,
                transport = transport,
                location = location,
            )
        }

        val added = courierRepository.add(courier)
        ensure(added) {
            Error.CourierAlreadyExists(courier.id).nel()
        }
        LOG.info("Courier with id: ${courier.id} was registered")

        courier.id
    }

    /**
     * Reason why the courier is not registered. An invalid detail of [RegisterCourierCommand] has
     * the business rule it breaks as its `cause`.
     */
    sealed class Error(
        override val message: String,
    ) : BusinessError {
        data class InvalidName(val cause: CourierName.Error) : Error(message = cause.message)

        data class InvalidLocation(val cause: Location.Error) : Error(message = cause.message)

        data class InvalidTransport(val cause: Transport.Error) : Error(message = cause.message)

        data class CourierAlreadyExists(val id: UUID) : Error(message = "Courier with id: $id already exists")
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(RegisterCourierUseCase::class.java)
    }
}

/**
 * The [transport] is a code of the transport, e.g. `BICYCLE`; the [location] is where the courier is now.
 */
data class RegisterCourierCommand(
    val name: String,
    val transport: String,
    val location: Location,
) {
    data class Location(
        val x: Int,
        val y: Int,
    )
}
