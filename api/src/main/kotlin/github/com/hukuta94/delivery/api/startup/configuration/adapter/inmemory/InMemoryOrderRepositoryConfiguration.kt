package github.com.hukuta94.delivery.api.startup.configuration.adapter.inmemory

import github.com.hukuta94.delivery.infrastructure.adapter.inmemory.OrderInMemoryRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class InMemoryOrderRepositoryConfiguration {

    @Bean
    open fun orderRepository() = OrderInMemoryRepository()
}