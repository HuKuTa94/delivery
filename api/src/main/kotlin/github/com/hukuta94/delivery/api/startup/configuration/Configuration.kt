package github.com.hukuta94.delivery.api.startup.configuration

import github.com.hukuta94.delivery.api.startup.configuration.adapter.http.ControllerConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.adapter.kafka.KafkaConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.adapter.orm.OrmRepositoryConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.adapter.scheduler.SchedulerConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.application.*
import github.com.hukuta94.delivery.api.startup.configuration.application.event.ApplicationEventsConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.application.usecase.UseCaseConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.domain.DomainServiceConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@EnableAutoConfiguration
@Import(
    UseCaseConfiguration::class,
    DomainServiceConfiguration::class,
    SchedulerConfiguration::class,
    PortConfiguration::class,
    ApplicationEventsConfiguration::class,
    KafkaConfiguration::class,
    OrmRepositoryConfiguration::class,
    ControllerConfiguration::class,
    QueriesConfiguration::class,
)
open class Configuration