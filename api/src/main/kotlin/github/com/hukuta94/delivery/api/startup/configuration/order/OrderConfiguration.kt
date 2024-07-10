package github.com.hukuta94.delivery.api.startup.configuration.order

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    OrderUseCaseConfiguration::class,
    OrderRepositoryConfiguration::class,
    OrderControllerConfiguration::class,
)
open class OrderConfiguration