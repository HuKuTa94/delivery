package github.com.hukuta94.delivery.api.startup.configuration.application

import github.com.hukuta94.delivery.core.application.event.domain.handler.DomainEventHandler
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventSerializer
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventPublisher
import github.com.hukuta94.delivery.core.application.event.domain.handler.OrderAssignedDomainEventHandler
import github.com.hukuta94.delivery.core.application.event.domain.handler.OrderCompletedDomainEventHandler
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEvent
import github.com.hukuta94.delivery.core.application.event.integration.handler.IntegrationEventHandler
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventPublisher
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventSerializer
import github.com.hukuta94.delivery.core.application.event.integration.handler.BasketConfirmedIntegrationEventHandler
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.core.port.BusProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@Suppress("UNCHECKED_CAST")
open class ApplicationEventHandlerConfiguration {

    @Bean
    open fun domainEventPublisher(
        busProducer: BusProducer,
    ) = DomainEventPublisher().apply {
          registerHandler(
              OrderAssignedDomainEventHandler(busProducer) as DomainEventHandler<DomainEvent>
          )
          registerHandler(
              OrderCompletedDomainEventHandler(busProducer) as DomainEventHandler<DomainEvent>
          )
      }

    @Bean
    open fun domainEventSerializer() = DomainEventSerializer()

    @Bean
    open fun integrationEventPublisher(
        createOrderUseCase: CreateOrderUseCase,
    ) = IntegrationEventPublisher().apply {
            registerHandler(
                BasketConfirmedIntegrationEventHandler(createOrderUseCase) as IntegrationEventHandler<IntegrationEvent>
            )
        }

    @Bean
    open fun integrationEventSerializer() = IntegrationEventSerializer()
}