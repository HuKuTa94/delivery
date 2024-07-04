package github.com.hukuta94.delivery.api.startup.configuration.courier

import github.com.hukuta94.delivery.infrastructure.adapter.inmemory.CourierInMemoryRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CourierRepositoryConfiguration {

    @Bean
    open fun courierRepository() = CourierInMemoryRepository()
}