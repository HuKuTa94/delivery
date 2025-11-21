package github.com.hukuta94.delivery.core.application.usecase.courier.impl

import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkFake
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryFake
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryFake
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.busyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.assignedOrder
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.core.domain.rule.impl.CompleteOrderBusinessRuleImpl
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MoveCouriersUseCaseImplTest : StringSpec({

    val orderRepository = OrderRepositoryFake()
    val courierRepository = CourierRepositoryFake()
    val completeOrderService = CompleteOrderBusinessRuleImpl()
    val unitOfWork = UnitOfWorkFake()

    val usecase = MoveCouriersUseCaseImpl(
        orderRepository = orderRepository,
        courierRepository = courierRepository,
        completeOrderBusinessRule = completeOrderService,
        unitOfWork = unitOfWork,
    )

    beforeTest {
        orderRepository.clear()
        courierRepository.clear()
    }

    "must move courier and complete order when courier reaches its location" {
        // Given
        val courierLocation = location(5, 5)
        val busyCourier = busyCourier(
            location = courierLocation,
        )

        val assignedOrder = assignedOrder(
            location = location(5, 6),
            courier = busyCourier,
        )
        orderRepository.add(assignedOrder)

        val couriers = (
                List(999) { freeCourier() } + // free couriers must be ignored
                listOf(busyCourier)
            ).shuffled()
        courierRepository.addAll(couriers)

        // When
        usecase.execute()

        // Then
        busyCourier.location shouldBe assignedOrder.location
        busyCourier.status shouldBe CourierStatus.FREE
        assignedOrder.status shouldBe OrderStatus.COMPLETED
    }
})
