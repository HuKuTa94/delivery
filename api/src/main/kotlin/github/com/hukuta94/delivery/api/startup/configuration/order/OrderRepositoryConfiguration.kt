package github.com.hukuta94.delivery.api.startup.configuration.order

import github.com.hukuta94.delivery.infrastructure.adapter.inmemory.OrderInMemoryRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrderRepositoryConfiguration {

    @Bean
    open fun orderRepository() = OrderInMemoryRepository()
}