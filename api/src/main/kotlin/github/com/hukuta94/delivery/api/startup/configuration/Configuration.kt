package github.com.hukuta94.delivery.api.startup.configuration

import github.com.hukuta94.delivery.api.startup.configuration.adapter.http.ControllerConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.adapter.kafka.KafkaConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.adapter.orm.OrmRepositoryConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.adapter.scheduler.SchedulerConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.application.CourierUseCaseConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.application.DomainEventHandlerConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.application.OrderUseCaseConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.application.PortConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.domain.DomainServiceConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@EnableAutoConfiguration
@Import(
    OrderUseCaseConfiguration::class,
    CourierUseCaseConfiguration::class,
    DomainServiceConfiguration::class,
    SchedulerConfiguration::class,
    PortConfiguration::class,
    DomainEventHandlerConfiguration::class,
    KafkaConfiguration::class,
    OrmRepositoryConfiguration::class,
    ControllerConfiguration::class,
)
open class Configuration