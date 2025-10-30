package github.com.hukuta94.delivery.core.application.usecase.courier.impl

import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkFake
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryFake
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryFake
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.newBusyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.newFreeCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.newAssignedOrder
import github.com.hukuta94.delivery.core.domain.common.newLocation
import github.com.hukuta94.delivery.core.domain.service.impl.CompleteOrderServiceImpl
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MoveCouriersUseCaseImplTest : StringSpec({

    val orderRepository = OrderRepositoryFake()
    val courierRepository = CourierRepositoryFake()
    val completeOrderService = CompleteOrderServiceImpl()
    val unitOfWork = UnitOfWorkFake()

    val sut = MoveCouriersUseCaseImpl(
        orderRepository = orderRepository,
        courierRepository = courierRepository,
        completeOrderService = completeOrderService,
        unitOfWork = unitOfWork,
    )

    beforeTest {
        orderRepository.clear()
        courierRepository.clear()
    }

    "must move courier and complete order when courier reaches its location" {
        // Given
        val courierLocation = newLocation(5, 5)
        val busyCourier = newBusyCourier(
            location = courierLocation,
        )

        val assignedOrder = newAssignedOrder(
            location = newLocation(5, 6),
            courier = busyCourier,
        )
        orderRepository.add(assignedOrder)

        val couriers = (
                List(999) { newFreeCourier() } + // free couriers must be ignored
                listOf(busyCourier)
            ).shuffled()
        courierRepository.addAll(couriers)

        // When
        sut.execute()

        // Then
        busyCourier.location shouldBe assignedOrder.location
        busyCourier.status shouldBe CourierStatus.FREE
        assignedOrder.status shouldBe OrderStatus.COMPLETED
    }
})
