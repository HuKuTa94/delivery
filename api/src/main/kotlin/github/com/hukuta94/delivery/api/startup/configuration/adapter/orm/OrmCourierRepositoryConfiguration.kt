package github.com.hukuta94.delivery.api.startup.configuration.adapter.orm

import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.jpa.CourierJpaRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.OrmCourierRepositoryAdapter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.OrmOutboxRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrmCourierRepositoryConfiguration {

    @Bean
    open fun courierRepository(
        courierJpaRepository: CourierJpaRepository,
        outboxRepository: OrmOutboxRepository,
    ) = OrmCourierRepositoryAdapter(
        courierJpaRepository = courierJpaRepository,
        outboxRepository = outboxRepository,
    )
}