package github.com.hukuta94.delivery.configuration.core

import github.com.hukuta94.delivery.configuration.core.application.event.ApplicationEventsConfiguration
import github.com.hukuta94.delivery.configuration.core.application.port.PortConfiguration
import github.com.hukuta94.delivery.configuration.core.application.query.QueryConfiguration
import github.com.hukuta94.delivery.configuration.core.application.usecase.UseCaseConfiguration
import github.com.hukuta94.delivery.configuration.core.domain.DomainServiceConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    PortConfiguration::class,
    UseCaseConfiguration::class,
    QueryConfiguration::class,
    DomainServiceConfiguration::class,
    ApplicationEventsConfiguration::class,
)
open class CoreConfiguration