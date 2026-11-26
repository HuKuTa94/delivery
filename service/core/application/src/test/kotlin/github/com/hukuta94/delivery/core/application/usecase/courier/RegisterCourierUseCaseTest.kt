package github.com.hukuta94.delivery.core.application.usecase.courier

import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryFake
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.application.usecase.courier.RegisterCourierUseCase.Error.CourierAlreadyExists
import github.com.hukuta94.delivery.core.application.usecase.courier.RegisterCourierUseCase.Error.InvalidLocation
import github.com.hukuta94.delivery.core.application.usecase.courier.RegisterCourierUseCase.Error.InvalidName
import github.com.hukuta94.delivery.core.application.usecase.courier.RegisterCourierUseCase.Error.InvalidTransport
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierName
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Transport
import github.com.hukuta94.delivery.core.domain.aggregate.courier.courierName
import github.com.hukuta94.delivery.core.domain.aggregate.courier.unknownTransportError
import github.com.hukuta94.delivery.core.domain.common.coordinatesOutOfRangeError
import github.com.hukuta94.delivery.core.domain.common.location
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

class RegisterCourierUseCaseTest : StringSpec({

    val courierRepository = CourierRepositoryFake()

    val sut = RegisterCourierUseCase(
        courierRepository = courierRepository,
    )

    fun registerCourierCommand(
        name: String = "Ivan",
        transport: String = Transport.BICYCLE.name,
        x: Int = 1,
        y: Int = 1,
    ) = RegisterCourierCommand(
        name = name,
        transport = transport,
        location = RegisterCourierCommand.Location(
            x = x,
            y = y,
        ),
    )

    beforeTest {
        courierRepository.clear()
    }

    "registered courier is free to take orders at the given location with the given name and transport" {
        // Given
        val command = registerCourierCommand(
            name = "Ivan",
            transport = Transport.CAR.name,
            x = 3,
            y = 7,
        )

        // When
        val courierId = sut(command).shouldBeRight()

        // Then
        val courier = courierRepository.getById(courierId)
        assertSoftly {
            courier.name shouldBe courierName(value = "Ivan")
            courier.transport shouldBe Transport.CAR
            courier.location shouldBe location(3, 7)
            courier.status shouldBe CourierStatus.FREE
        }
    }

    "couriers with the same name are registered as different couriers" {
        // Given
        val command = registerCourierCommand(name = "Ivan")

        // When
        val courierIds = List(2) { sut(command).shouldBeRight() }

        // Then
        assertSoftly {
            courierIds.shouldBeUnique()
            courierRepository.getAllFree().map { it.id } shouldContainExactlyInAnyOrder courierIds
        }
    }

    "courier with an invalid name is not registered" {
        // Given
        val command = registerCourierCommand(name = " ")

        // When
        val errors = sut(command).shouldBeLeft()

        // Then
        assertSoftly {
            errors shouldContainExactly listOf(InvalidName(cause = CourierName.Error.NameCanNotBeEmpty))
            courierRepository.getAllFree().shouldBeEmpty()
        }
    }

    "courier with an unknown transport is not registered" {
        // Given
        val command = registerCourierCommand(transport = "BIKE")

        // When
        val errors = sut(command).shouldBeLeft()

        // Then
        assertSoftly {
            errors shouldContainExactly listOf(InvalidTransport(cause = unknownTransportError(code = "BIKE")))
            courierRepository.getAllFree().shouldBeEmpty()
        }
    }

    "courier outside the delivery area is not registered" {
        // Given
        val command = registerCourierCommand(
            x = 0,
            y = 11,
        )

        // When
        val errors = sut(command).shouldBeLeft()

        // Then
        assertSoftly {
            errors shouldContainExactly listOf(InvalidLocation(cause = coordinatesOutOfRangeError(x = 0, y = 11)))
            courierRepository.getAllFree().shouldBeEmpty()
        }
    }

    "every invalid detail of a courier is reported at once" {
        // Given
        val command = registerCourierCommand(
            name = " ",
            transport = "BIKE",
            x = 0,
            y = 11,
        )

        // When
        val errors = sut(command).shouldBeLeft()

        // Then
        errors shouldContainExactly listOf(
            InvalidName(cause = CourierName.Error.NameCanNotBeEmpty),
            InvalidTransport(cause = unknownTransportError(code = "BIKE")),
            InvalidLocation(cause = coordinatesOutOfRangeError(x = 0, y = 11)),
        )
    }

    "courier is not registered when a courier with the same id is already stored" {
        // Given
        val couriersWithTakenId = mutableListOf<Courier>()
        val repositoryWithTakenIds = object : CourierRepositoryPort by courierRepository {
            override fun add(aggregate: Courier): Boolean {
                couriersWithTakenId += aggregate
                return false
            }
        }
        val sutWithTakenIds = RegisterCourierUseCase(
            courierRepository = repositoryWithTakenIds,
        )

        // When
        val errors = sutWithTakenIds(registerCourierCommand()).shouldBeLeft()

        // Then
        errors shouldContainExactly listOf(CourierAlreadyExists(id = couriersWithTakenId.single().id))
    }
})
