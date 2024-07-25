package github.com.hukuta94.delivery.api.startup.configuration.application

import github.com.hukuta94.delivery.core.application.usecase.order.AssignCourierToOrderUseCase
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase
import github.com.hukuta94.delivery.core.application.usecase.order.impl.AssignCourierToOrderUseCaseImpl
import github.com.hukuta94.delivery.core.application.usecase.order.impl.CreateOrderUseCaseImpl
import github.com.hukuta94.delivery.core.domain.service.DispatchService
import github.com.hukuta94.delivery.core.port.CourierRepository
import github.com.hukuta94.delivery.core.port.GetLocationPort
import github.com.hukuta94.delivery.core.port.OrderRepository
import github.com.hukuta94.delivery.core.port.UnitOfWork
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrderUseCaseConfiguration {

    @Bean
    open fun createOrderUseCase(
        orderRepository: OrderRepository,
        getLocationPort: GetLocationPort,
    ): CreateOrderUseCase = CreateOrderUseCaseImpl(
        orderRepository = orderRepository,
        getLocationPort = getLocationPort,
    )

    @Bean
    open fun AssignCourierToOrderUseCase(
        orderRepository: OrderRepository,
        courierRepository: CourierRepository,
        dispatchService: DispatchService,
        unitOfWork: UnitOfWork,
    ): AssignCourierToOrderUseCase = AssignCourierToOrderUseCaseImpl(
        orderRepository = orderRepository,
        courierRepository = courierRepository,
        dispatchService = dispatchService,
        unitOfWork = unitOfWork,
    )
}