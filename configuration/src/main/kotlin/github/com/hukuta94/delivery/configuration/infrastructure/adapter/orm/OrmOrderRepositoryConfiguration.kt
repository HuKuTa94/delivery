package github.com.hukuta94.delivery.configuration.infrastructure.adapter.orm

import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.domain.OrderJpaRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.domain.OrmOrderRepositoryAdapter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.event.OrmOutboxEventRepositoryAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrmOrderRepositoryConfiguration {

    @Bean
    open fun orderRepository(
        orderJpaRepository: OrderJpaRepository,
        outboxRepository: OrmOutboxEventRepositoryAdapter,
    ) = OrmOrderRepositoryAdapter(
        orderJpaRepository = orderJpaRepository,
        outboxRepository = outboxRepository,
    )
}