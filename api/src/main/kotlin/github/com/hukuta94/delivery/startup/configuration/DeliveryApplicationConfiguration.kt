package github.com.hukuta94.delivery.startup.configuration

import github.com.hukuta94.delivery.startup.configuration.api.ApiConfiguration
import github.com.hukuta94.delivery.startup.configuration.core.CoreConfiguration
import github.com.hukuta94.delivery.startup.configuration.infrastructure.InfrastructureConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@EnableAutoConfiguration
@Import(
    ApiConfiguration::class,
    CoreConfiguration::class,
    InfrastructureConfiguration::class,
)
open class DeliveryApplicationConfiguration