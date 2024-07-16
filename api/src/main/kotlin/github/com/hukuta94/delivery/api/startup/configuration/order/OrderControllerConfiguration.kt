package github.com.hukuta94.delivery.api.startup.configuration.order

import github.com.hukuta94.delivery.api.adapter.http.order.OrderControllerV1
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase
import github.com.hukuta94.delivery.core.application.usecase.order.GetNotCompletedOrdersQuery
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrderControllerConfiguration {
    @Bean
    open fun orderControllerV1(
        createOrderUseCase: CreateOrderUseCase,
        getNotCompletedOrdersQuery: GetNotCompletedOrdersQuery,
    ) = OrderControllerV1(
        createOrderUseCase = createOrderUseCase,
        getNotCompletedOrdersQuery = getNotCompletedOrdersQuery,
    )
}