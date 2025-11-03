package github.com.hukuta94.delivery.configuration.infrastructure.orm

import github.com.hukuta94.delivery.infrastructure.orm.springjpa.repository.domain.CourierJpaRepository
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.repository.domain.OrmCourierRepositoryAdapter
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.repository.event.SpringJpaOutboxEventRepositoryAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class SpringJpaCourierRepositoryConfiguration {

    @Bean
    open fun courierRepository(
        courierJpaRepository: CourierJpaRepository,
        outboxRepository: SpringJpaOutboxEventRepositoryAdapter,
    ) = OrmCourierRepositoryAdapter(
        courierJpaRepository = courierJpaRepository,
        outboxRepository = outboxRepository,
    )
}