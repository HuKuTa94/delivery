package github.com.hukuta94.delivery.configuration.core.application.event

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    DomainEventsConfiguration::class,
    IntegrationEventsConfiguration::class,
)
open class ApplicationEventsConfiguration