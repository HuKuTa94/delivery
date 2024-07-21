package github.com.hukuta94.delivery.api.startup.configuration.order

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    OrderUseCaseConfiguration::class,
    OrderControllerConfiguration::class,
)
open class OrderConfiguration