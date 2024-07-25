package github.com.hukuta94.delivery.api.startup.configuration.adapter.orm

import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.CourierJpaRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.CourierRepositoryAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrmCourierRepositoryConfiguration {

    @Bean
    open fun courierRepository(
        courierJpaRepository: CourierJpaRepository,
    ) = CourierRepositoryAdapter(
        courierJpaRepository = courierJpaRepository,
    )
}