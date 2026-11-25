package github.com.hukuta94.delivery.core.application.usecase.order

import github.com.hukuta94.delivery.core.application.port.GetLocationPortFake
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryFake
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.assignedOrder
import github.com.hukuta94.delivery.core.domain.common.coordinatesOutOfRangeError
import github.com.hukuta94.delivery.core.domain.common.location
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.util.UUID

class CreateOrderUseCaseTest : StringSpec({

    val orderRepository = OrderRepositoryFake()
    val getLocationPort = GetLocationPortFake()

    val sut = CreateOrderUseCase(
        orderRepository = orderRepository,
        getLocationPort = getLocationPort,
    )

    beforeTest {
        orderRepository.clear()
        getLocationPort.clear()
    }

    "confirmed basket becomes a created order without a courier at the location of its street" {
        // Given
        val street = "Tverskaya"
        val streetLocation = location(3, 7)
        getLocationPort.locate(
            street = street,
            location = streetLocation,
        )
        val command = CreateOrderCommand(
            basketId = UUID.randomUUID(),
            street = street,
        )

        // When
        sut(command)

        // Then
        val order = orderRepository.getById(command.basketId)
        assertSoftly {
            order.location shouldBe streetLocation
            order.status shouldBe OrderStatus.CREATED
            order.courierId.shouldBeNull()
        }
    }

    "every basket gets its own order, also when baskets share a street" {
        // Given
        val firstStreet = "Tverskaya"
        val secondStreet = "Arbat"
        val firstStreetLocation = location(2, 2)
        val secondStreetLocation = location(8, 8)
        getLocationPort.locate(
            street = firstStreet,
            location = firstStreetLocation,
        )
        getLocationPort.locate(
            street = secondStreet,
            location = secondStreetLocation,
        )
        val commands = listOf(firstStreet, firstStreet, secondStreet).map { street ->
            CreateOrderCommand(
                basketId = UUID.randomUUID(),
                street = street,
            )
        }

        // When
        commands.forEach { sut(it) }

        // Then
        orderRepository.getAllCreated().map { it.id to it.location } shouldContainExactlyInAnyOrder listOf(
            commands[0].basketId to firstStreetLocation,
            commands[1].basketId to firstStreetLocation,
            commands[2].basketId to secondStreetLocation,
        )
    }

    "repeated basket leaves the existing order as it is" {
        // Given
        val street = "Tverskaya"
        val existingOrder = assignedOrder(location = location(1, 1))
        orderRepository.add(existingOrder)
        getLocationPort.locate(
            street = street,
            location = location(9, 9),
        )
        val command = CreateOrderCommand(
            basketId = existingOrder.id,
            street = street,
        )

        // When
        sut(command)

        // Then
        val storedOrder = orderRepository.getById(existingOrder.id)
        assertSoftly {
            storedOrder shouldBeSameInstanceAs existingOrder
            storedOrder.status shouldBe OrderStatus.ASSIGNED
            orderRepository.updatedIds.shouldBeEmpty()
        }
    }

    "order is not created when the street can't be located" {
        // Given
        val street = "Nowhere"
        val locationError = coordinatesOutOfRangeError()
        getLocationPort.failToLocate(
            street = street,
            error = locationError,
        )
        val command = CreateOrderCommand(
            basketId = UUID.randomUUID(),
            street = street,
        )

        // When
        val exception = shouldThrow<IllegalStateException> { sut(command) }

        // Then
        assertSoftly {
            exception.message shouldBe locationError.message
            orderRepository.existsById(command.basketId) shouldBe false
        }
    }
})
