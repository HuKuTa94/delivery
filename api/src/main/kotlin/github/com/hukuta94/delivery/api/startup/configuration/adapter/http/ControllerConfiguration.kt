package github.com.hukuta94.delivery.api.startup.configuration.adapter.http

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@EnableWebMvc
@Configuration
@Import(
    OrderControllerConfiguration::class,
    CourierControllerConfiguration::class,
)
open class ControllerConfiguration