package github.com.hukuta94.delivery.bootstrap.core

import github.com.hukuta94.delivery.bootstrap.core.application.event.ApplicationEventsConfiguration
import github.com.hukuta94.delivery.bootstrap.core.application.port.PortConfiguration
import github.com.hukuta94.delivery.bootstrap.core.application.query.SpringJpaQueryConfiguration
import github.com.hukuta94.delivery.bootstrap.core.application.usecase.UseCaseConfiguration
import github.com.hukuta94.delivery.bootstrap.core.domain.DomainServiceConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    PortConfiguration::class,
    UseCaseConfiguration::class,
    SpringJpaQueryConfiguration::class,
    DomainServiceConfiguration::class,
    ApplicationEventsConfiguration::class,
)
open class CoreConfiguration
