package github.com.hukuta94.delivery.api.startup.configuration.courier

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    CourierUseCaseConfiguration::class,
    CourierRepositoryConfiguration::class,
    CourierControllerConfiguration::class,
)
open class CourierConfiguration