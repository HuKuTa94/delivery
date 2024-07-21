package github.com.hukuta94.delivery.api.startup.configuration.application

import github.com.hukuta94.delivery.core.application.event.DomainEventPublisher
import github.com.hukuta94.delivery.core.application.event.impl.DomainEventPublisherImpl
import github.com.hukuta94.delivery.core.application.event.impl.OrderAssignedDomainEventHandler
import github.com.hukuta94.delivery.core.application.event.impl.OrderCompletedDomainEventHandler
import github.com.hukuta94.delivery.core.port.BusProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class DomainEventHandlerConfiguration {

    @Bean
    open fun domainEventPublisher(
        busProducer: BusProducer,
    ): DomainEventPublisher {
      return DomainEventPublisherImpl().apply {
          registerListener(OrderAssignedDomainEventHandler(busProducer))
          registerListener(OrderCompletedDomainEventHandler(busProducer))
      }
    }
}