package github.com.hukuta94.delivery.configuration.api.http

import github.com.hukuta94.delivery.api.http.order.OrderControllerV1
import github.com.hukuta94.delivery.core.application.query.order.GetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.core.application.usecase.event.SaveIntegrationEventUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrderRestConfiguration {
    @Bean
    open fun orderControllerV1(
        getNotCompletedOrdersQuery: GetNotCompletedOrdersQuery,
        saveIntegrationEventUseCase: SaveIntegrationEventUseCase,
    ) = OrderControllerV1(
        getNotCompletedOrdersQuery = getNotCompletedOrdersQuery,
        saveIntegrationEventUseCase = saveIntegrationEventUseCase,
    )
}