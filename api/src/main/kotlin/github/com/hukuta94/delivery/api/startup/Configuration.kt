package github.com.hukuta94.delivery.api.startup

import github.com.hukuta94.delivery.infrastructure.adapter.inmemory.CourierInMemoryRepository
import github.com.hukuta94.delivery.infrastructure.adapter.inmemory.OrderInMemoryRepository
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@Configuration
@EnableWebMvc
@EnableAutoConfiguration
open class Configuration {
    @Bean
    open fun orderRepository() = OrderInMemoryRepository()

    @Bean
    open fun courierRepository() = CourierInMemoryRepository()
}