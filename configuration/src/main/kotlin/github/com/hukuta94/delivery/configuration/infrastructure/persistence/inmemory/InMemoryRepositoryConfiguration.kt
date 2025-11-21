package github.com.hukuta94.delivery.configuration.infrastructure.persistence.inmemory

import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryPort
import github.com.hukuta94.delivery.infrastructure.persistence.inmemory.InMemoryCourierRepository
import github.com.hukuta94.delivery.infrastructure.persistence.inmemory.InMemoryOrderRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class InMemoryRepositoryConfiguration {
    @Bean
    open fun orderRepository(): OrderRepositoryPort = InMemoryOrderRepository()

    @Bean
    open fun courierRepository(): CourierRepositoryPort = InMemoryCourierRepository()
}
