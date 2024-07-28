package github.com.hukuta94.delivery.api.startup.configuration.adapter.orm

import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.jpa.OrderJpaRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.OrmOrderRepositoryAdapter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.box.OrmOutboxRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrmOrderRepositoryConfiguration {

    @Bean
    open fun orderRepository(
        orderJpaRepository: OrderJpaRepository,
        outboxRepository: OrmOutboxRepository,
    ) = OrmOrderRepositoryAdapter(
        orderJpaRepository = orderJpaRepository,
        outboxRepository = outboxRepository,
    )
}