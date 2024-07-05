package github.com.hukuta94.delivery.api.startup.configuration.order

import github.com.hukuta94.delivery.core.application.usecase.order.command.impl.AssignCourierToOrderCommandImpl
import github.com.hukuta94.delivery.core.application.usecase.order.command.impl.CreateOrderCommandImpl
import github.com.hukuta94.delivery.core.application.usecase.order.query.impl.GetNotCompletedOrdersQueryImpl
import github.com.hukuta94.delivery.core.domain.service.DispatchService
import github.com.hukuta94.delivery.core.port.CourierRepository
import github.com.hukuta94.delivery.core.port.OrderRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrderUseCaseConfiguration {

    @Bean
    open fun createOrderCommand(
        orderRepository: OrderRepository
    ) = CreateOrderCommandImpl(
        orderRepository = orderRepository
    )

    @Bean
    open fun getNotCompletedOrdersQuery(
        orderRepository: OrderRepository
    ) = GetNotCompletedOrdersQueryImpl(
        orderRepository = orderRepository
    )

    @Bean
    open fun assignCourierToOrderCommand(
        orderRepository: OrderRepository,
        courierRepository: CourierRepository,
        dispatchService: DispatchService,
    ) = AssignCourierToOrderCommandImpl(
        orderRepository = orderRepository,
        courierRepository = courierRepository,
        dispatchService = dispatchService,
    )
}