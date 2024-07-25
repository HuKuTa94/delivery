package github.com.hukuta94.delivery.api.startup.configuration.adapter.orm

import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.jpa.OrderJpaRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.OrmOrderRepositoryAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrmOrderRepositoryConfiguration {

    @Bean
    open fun orderRepository(
        orderJpaRepository: OrderJpaRepository,
    ) = OrmOrderRepositoryAdapter(
        orderJpaRepository = orderJpaRepository,
    )
}