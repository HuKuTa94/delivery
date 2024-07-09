package github.com.hukuta94.delivery.api.startup.configuration

import github.com.hukuta94.delivery.api.startup.configuration.courier.CourierControllerConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.courier.CourierRepositoryConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.courier.CourierUseCaseConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.order.OrderControllerConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.order.OrderRepositoryConfiguration
import github.com.hukuta94.delivery.api.startup.configuration.order.OrderUseCaseConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@Configuration
@EnableWebMvc
@EnableAutoConfiguration
@Import(
    DomainServiceConfiguration::class,

    OrderRepositoryConfiguration::class,
    OrderUseCaseConfiguration::class,
    OrderControllerConfiguration::class,

    CourierRepositoryConfiguration::class,
    CourierUseCaseConfiguration::class,
    CourierControllerConfiguration::class,
)
open class Configuration {

}