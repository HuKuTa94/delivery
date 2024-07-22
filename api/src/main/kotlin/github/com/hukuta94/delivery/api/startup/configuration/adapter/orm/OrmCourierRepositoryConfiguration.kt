package github.com.hukuta94.delivery.api.startup.configuration.adapter.orm

import github.com.hukuta94.delivery.core.application.event.DomainEventPublisher
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.CourierJpaRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.CourierRepositoryAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrmCourierRepositoryConfiguration {

    @Bean
    open fun courierRepository(
        domainEventPublisher: DomainEventPublisher,
        courierJpaRepository: CourierJpaRepository,
    ) = CourierRepositoryAdapter(
        domainEventPublisher = domainEventPublisher,
        courierJpaRepository = courierJpaRepository,
    )
}