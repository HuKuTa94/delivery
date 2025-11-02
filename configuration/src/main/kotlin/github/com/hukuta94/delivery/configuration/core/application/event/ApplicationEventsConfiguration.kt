package github.com.hukuta94.delivery.configuration.core.application.event

import github.com.hukuta94.delivery.core.application.event.ApplicationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.ApplicationEventHandler
import github.com.hukuta94.delivery.core.application.event.ApplicationEventPublisher
import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.event.handler.BasketConfirmedIntegrationDomainEventHandler
import github.com.hukuta94.delivery.core.application.event.handler.OrderAssignedDomainEventHandler
import github.com.hukuta94.delivery.core.application.event.handler.OrderCompletedDomainEventHandler
import github.com.hukuta94.delivery.core.application.port.BusProducerPort
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ApplicationEventsConfiguration {

    @Bean
    open fun applicationEventSerializer() = ApplicationEventSerializer()

    @Bean
    open fun applicationEventDeserializer() = ApplicationEventDeserializer()

    @Bean
    open fun orderAssignedDomainEventHandler(
        busProducerPort: BusProducerPort,
    ) = OrderAssignedDomainEventHandler(busProducerPort)

    @Bean
    open fun orderCompletedDomainEventHandler(
        busProducerPort: BusProducerPort,
    ) = OrderCompletedDomainEventHandler(busProducerPort)

    @Bean
    open fun basketConfirmedIntegrationDomainEventHandler(
        createOrderUseCase: CreateOrderUseCase,
    ) = BasketConfirmedIntegrationDomainEventHandler(createOrderUseCase)

    @Bean
    open fun applicationEventPublisher(
        applicationEventHandlers: List<ApplicationEventHandler<*>>
    ) = ApplicationEventPublisher(
        handlers = applicationEventHandlers,
    )
}