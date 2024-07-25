package github.com.hukuta94.delivery.api.startup.configuration.adapter.orm

import github.com.hukuta94.delivery.core.application.event.DomainEventSerializer
import github.com.hukuta94.delivery.core.port.UnitOfWork
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.DomainEventConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.OrmOutboxRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.OrmUnitOfWork
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.jpa.OutboxJpaRepository
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EntityScan(
    basePackages = [
        "github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity"
    ]
)
@EnableJpaRepositories(
    basePackages = [
        "github.com.hukuta94.delivery.infrastructure.adapter.orm.repository"
    ]
)
@Import(
    OrmOrderRepositoryConfiguration::class,
    OrmCourierRepositoryConfiguration::class,
)
open class OrmRepositoryConfiguration {

    @Bean
    open fun ormUnitOfWork(): UnitOfWork {
        return OrmUnitOfWork()
    }

    @Bean
    open fun domainEventConverter(
        domainEventSerializer: DomainEventSerializer
    ) = DomainEventConverter(
        domainEventSerializer = domainEventSerializer
    )

    @Bean
    open fun ormOutboxRepository(
        outboxJpaRepository: OutboxJpaRepository,
        domainEventConverter: DomainEventConverter,
    ) = OrmOutboxRepository(
        outboxJpaRepository = outboxJpaRepository,
        domainEventConverter = domainEventConverter,
    )
}