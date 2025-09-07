package github.com.hukuta94.delivery.configuration.infrastructure.inmemory

import github.com.hukuta94.delivery.infrastructure.inmemory.CourierInMemoryRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class InMemoryCourierRepositoryConfiguration {

    @Bean
    open fun courierRepository() = CourierInMemoryRepository()
}