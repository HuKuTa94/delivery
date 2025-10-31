package github.com.hukuta94.delivery.configuration.core.application.usecase

import github.com.hukuta94.delivery.core.application.usecase.order.AssignCourierToOrderUseCase
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase
import github.com.hukuta94.delivery.core.application.usecase.order.impl.AssignCourierToOrderUseCaseImpl
import github.com.hukuta94.delivery.core.application.usecase.order.impl.CreateOrderUseCaseImpl
import github.com.hukuta94.delivery.core.domain.rule.DispatchOrderToCourierBusinessRule
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.application.port.GetLocationPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrderUseCaseConfiguration {

    @Bean
    open fun createOrderUseCase(
        orderRepository: OrderRepositoryPort,
        getLocationPort: GetLocationPort,
    ): CreateOrderUseCase = CreateOrderUseCaseImpl(
        orderRepository = orderRepository,
        getLocationPort = getLocationPort,
    )

    @Bean
    open fun AssignCourierToOrderUseCase(
        orderRepository: OrderRepositoryPort,
        courierRepository: CourierRepositoryPort,
        dispatchOrderToCourierBusinessRule: DispatchOrderToCourierBusinessRule,
        unitOfWork: UnitOfWorkPort,
    ): AssignCourierToOrderUseCase = AssignCourierToOrderUseCaseImpl(
        orderRepository = orderRepository,
        courierRepository = courierRepository,
        dispatchOrderToCourierBusinessRule = dispatchOrderToCourierBusinessRule,
        unitOfWork = unitOfWork,
    )
}