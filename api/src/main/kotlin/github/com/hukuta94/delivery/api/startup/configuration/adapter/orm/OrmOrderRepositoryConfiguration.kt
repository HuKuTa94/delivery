package github.com.hukuta94.delivery.api.startup.configuration.adapter.orm

import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.OrderJpaRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.OrderRepositoryAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrmOrderRepositoryConfiguration {

    @Bean
    open fun orderRepository(
        orderJpaRepository: OrderJpaRepository,
    ) = OrderRepositoryAdapter(
        orderJpaRepository = orderJpaRepository,
    )
}