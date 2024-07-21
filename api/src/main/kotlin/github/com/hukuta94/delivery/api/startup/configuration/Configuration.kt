package github.com.hukuta94.delivery.api.startup.configuration

import github.com.hukuta94.delivery.api.startup.configuration.adapter.KafkaConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.adapter.inmemory.InMemoryRepositoryConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.courier.CourierConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.order.OrderConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.scheduler.SchedulerConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@Configuration
@EnableWebMvc
@EnableAutoConfiguration
@Import(
    OrderConfiguration::class,
    CourierConfiguration::class,
    DomainServiceConfiguration::class,
    SchedulerConfiguration::class,
    PortConfiguration::class,
    DomainEventHandlerConfiguration::class,
    KafkaConfiguration::class,
    InMemoryRepositoryConfiguration::class,
)
open class Configuration