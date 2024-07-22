package github.com.hukuta94.delivery.api.startup.configuration.application

import github.com.hukuta94.delivery.core.application.usecase.courier.MoveCouriersUseCase
import github.com.hukuta94.delivery.core.application.usecase.courier.impl.MoveCouriersUseCaseImpl
import github.com.hukuta94.delivery.core.domain.service.CompleteOrderService
import github.com.hukuta94.delivery.core.port.CourierRepository
import github.com.hukuta94.delivery.core.port.OrderRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CourierUseCaseConfiguration {

    @Bean
    open fun MoveCouriersUseCase(
        orderRepository: OrderRepository,
        courierRepository: CourierRepository,
        completeOrderService: CompleteOrderService,
    ): MoveCouriersUseCase = MoveCouriersUseCaseImpl(
        orderRepository = orderRepository,
        courierRepository = courierRepository,
        completeOrderService = completeOrderService,
    )
}