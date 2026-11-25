package github.com.hukuta94.delivery.bootstrap.core.application.event

import github.com.hukuta94.delivery.core.application.event.EventDeserializer
import github.com.hukuta94.delivery.core.application.event.EventHandler
import github.com.hukuta94.delivery.core.application.event.EventPublisher
import github.com.hukuta94.delivery.core.application.event.EventSerializer
import github.com.hukuta94.delivery.core.application.event.handler.BasketConfirmedEventHandler
import github.com.hukuta94.delivery.core.application.event.handler.OrderAssignedEventHandler
import github.com.hukuta94.delivery.core.application.event.handler.OrderCompletedEventHandler
import github.com.hukuta94.delivery.core.application.port.BusProducerPort
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ApplicationEventsConfiguration {

    @Bean
    open fun applicationEventSerializer() = EventSerializer()

    @Bean
    open fun applicationEventDeserializer() = EventDeserializer()

    @Bean
    open fun orderAssignedDomainEventHandler(
        busProducerPort: BusProducerPort,
    ) = OrderAssignedEventHandler(busProducerPort)

    @Bean
    open fun orderCompletedDomainEventHandler(
        busProducerPort: BusProducerPort,
    ) = OrderCompletedEventHandler(busProducerPort)

    @Bean
    open fun basketConfirmedIntegrationDomainEventHandler(
        createOrderUseCase: CreateOrderUseCase,
    ) = BasketConfirmedEventHandler(createOrderUseCase)

    @Bean
    open fun applicationEventPublisher(
        eventHandlers: List<EventHandler<*>>
    ) = EventPublisher(
        handlers = eventHandlers,
    )
}
