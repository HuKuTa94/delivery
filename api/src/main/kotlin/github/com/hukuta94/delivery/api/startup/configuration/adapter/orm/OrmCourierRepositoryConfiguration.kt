package github.com.hukuta94.delivery.api.startup.configuration.adapter.orm

import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.domain.CourierJpaRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.domain.OrmCourierRepositoryAdapter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.event.OrmOutboxEventRepositoryAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrmCourierRepositoryConfiguration {

    @Bean
    open fun courierRepository(
        courierJpaRepository: CourierJpaRepository,
        outboxRepository: OrmOutboxEventRepositoryAdapter,
    ) = OrmCourierRepositoryAdapter(
        courierJpaRepository = courierJpaRepository,
        outboxRepository = outboxRepository,
    )
}