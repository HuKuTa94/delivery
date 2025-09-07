package github.com.hukuta94.delivery.configuration.infrastructure.inmemory

import github.com.hukuta94.delivery.infrastructure.inmemory.OrderInMemoryRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class InMemoryOrderRepositoryConfiguration {

    @Bean
    open fun orderRepository() = OrderInMemoryRepository()
}