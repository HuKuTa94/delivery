package github.com.hukuta94.delivery.startup.configuration.core.application.event

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventDeserializer
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventPublisher
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventSerializer
import github.com.hukuta94.delivery.core.application.event.domain.handler.DomainEventHandler
import github.com.hukuta94.delivery.core.application.event.domain.handler.OrderAssignedDomainEventHandler
import github.com.hukuta94.delivery.core.application.event.domain.handler.OrderCompletedDomainEventHandler
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.core.port.BusProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@Suppress("UNCHECKED_CAST")
open class DomainEventsConfiguration {

    @Bean
    open fun domainEventSerializer() = DomainEventSerializer()

    @Bean
    open fun domainEventDeserializer() = DomainEventDeserializer()

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
}