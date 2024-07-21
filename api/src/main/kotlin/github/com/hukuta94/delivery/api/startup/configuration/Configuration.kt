package github.com.hukuta94.delivery.api.startup.configuration

import github.com.hukuta94.delivery.api.startup.configuration.adapter.kafka.KafkaConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.adapter.http.ControllerConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.adapter.orm.OrmRepositoryConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.courier.CourierConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.order.OrderConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.scheduler.SchedulerConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@EnableAutoConfiguration
@Import(
    OrderConfiguration::class,
    CourierConfiguration::class,
    DomainServiceConfiguration::class,
    SchedulerConfiguration::class,
    PortConfiguration::class,
    DomainEventHandlerConfiguration::class,
    KafkaConfiguration::class,
    OrmRepositoryConfiguration::class,
    ControllerConfiguration::class,
)
open class Configuration