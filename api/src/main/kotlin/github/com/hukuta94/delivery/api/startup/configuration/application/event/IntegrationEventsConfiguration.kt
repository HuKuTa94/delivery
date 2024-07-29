package github.com.hukuta94.delivery.api.startup.configuration.application.event

import github.com.hukuta94.delivery.core.domain.IntegrationEvent
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventPublisher
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventSerializer
import github.com.hukuta94.delivery.core.application.event.integration.handler.BasketConfirmedIntegrationEventHandler
import github.com.hukuta94.delivery.core.application.event.integration.handler.IntegrationEventHandler
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@Suppress("UNCHECKED_CAST")
open class IntegrationEventsConfiguration {

    @Bean
    open fun integrationEventSerializer() = IntegrationEventSerializer()

    @Bean
    open fun integrationEventDeserializer() = IntegrationEventDeserializer()

    @Bean
    open fun integrationEventPublisher(
        createOrderUseCase: CreateOrderUseCase,
    ) = IntegrationEventPublisher().apply {
        registerHandler(
            BasketConfirmedIntegrationEventHandler(createOrderUseCase) as IntegrationEventHandler<IntegrationEvent>
        )
    }
}