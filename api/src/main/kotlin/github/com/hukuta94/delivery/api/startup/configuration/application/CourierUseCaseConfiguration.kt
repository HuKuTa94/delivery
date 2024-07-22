package github.com.hukuta94.delivery.api.startup.configuration.application

import github.com.hukuta94.delivery.core.application.usecase.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.application.usecase.courier.GetFreeCouriersQuery
import github.com.hukuta94.delivery.core.application.usecase.courier.MoveCouriersUseCase
import github.com.hukuta94.delivery.core.application.usecase.courier.impl.MoveCouriersUseCaseImpl
import github.com.hukuta94.delivery.core.application.usecase.courier.impl.GetBusyCouriersQueryImpl
import github.com.hukuta94.delivery.core.application.usecase.courier.impl.GetFreeCouriersQueryImpl
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

    @Bean
    open fun getFreeCouriersQuery(
        courierRepository: CourierRepository
    ): GetFreeCouriersQuery = GetFreeCouriersQueryImpl(
        courierRepository = courierRepository
    )

    @Bean
    open fun getBusyCouriersQuery(
        courierRepository: CourierRepository
    ): GetBusyCouriersQuery = GetBusyCouriersQueryImpl(
        courierRepository = courierRepository
    )
}