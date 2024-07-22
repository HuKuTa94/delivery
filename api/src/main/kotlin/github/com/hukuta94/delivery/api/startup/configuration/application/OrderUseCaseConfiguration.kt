package github.com.hukuta94.delivery.api.startup.configuration.application

import github.com.hukuta94.delivery.core.application.usecase.order.AssignCourierToOrderUseCase
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase
import github.com.hukuta94.delivery.core.application.query.order.GetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.core.application.usecase.order.impl.AssignCourierToOrderUseCaseImpl
import github.com.hukuta94.delivery.core.application.usecase.order.impl.CreateOrderUseCaseImpl
import github.com.hukuta94.delivery.core.application.usecase.order.impl.GetNotCompletedOrdersQueryImpl
import github.com.hukuta94.delivery.core.domain.service.DispatchService
import github.com.hukuta94.delivery.core.port.CourierRepository
import github.com.hukuta94.delivery.core.port.GetLocationPort
import github.com.hukuta94.delivery.core.port.OrderRepository
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
    open fun getNotCompletedOrdersQuery(
        orderRepository: OrderRepository
    ): GetNotCompletedOrdersQuery = GetNotCompletedOrdersQueryImpl(
        orderRepository = orderRepository
    )

    @Bean
    open fun AssignCourierToOrderUseCase(
        orderRepository: OrderRepository,
        courierRepository: CourierRepository,
        dispatchService: DispatchService,
    ): AssignCourierToOrderUseCase = AssignCourierToOrderUseCaseImpl(
        orderRepository = orderRepository,
        courierRepository = courierRepository,
        dispatchService = dispatchService,
    )
}