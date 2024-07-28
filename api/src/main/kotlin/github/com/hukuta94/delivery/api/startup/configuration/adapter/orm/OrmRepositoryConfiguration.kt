package github.com.hukuta94.delivery.api.startup.configuration.adapter.orm

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventSerializer
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventPublisher
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventPublisher
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventSerializer
import github.com.hukuta94.delivery.core.port.UnitOfWork
import github.com.hukuta94.delivery.core.port.repository.box.InboxRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.job.PollToPublishInboxMessagesJob
import github.com.hukuta94.delivery.infrastructure.adapter.orm.job.PollToPublishOutboxMessagesJob
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.DomainEventConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.IntegrationEventConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.OrmOutboxRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.OrmUnitOfWork
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.box.OrmInboxRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.jpa.InboxJpaRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.jpa.OutboxJpaRepository
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
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
@Suppress("SpringJavaInjectionPointsAutowiringInspection")
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

    @Bean
    open fun pollToPublishOutboxMessagesJob(
        outboxJpaRepository: OutboxJpaRepository,
        domainEventPublisher: DomainEventPublisher,
        domainEventSerializer: DomainEventSerializer,
    ) = PollToPublishOutboxMessagesJob(
        outboxJpaRepository = outboxJpaRepository,
        domainEventPublisher = domainEventPublisher,
        domainEventSerializer = domainEventSerializer,
    )

    @Bean
    open fun integrationEventConverter(
        integrationEventSerializer: IntegrationEventSerializer,
    ) = IntegrationEventConverter(
        integrationEventSerializer = integrationEventSerializer,
    )

    @Bean
    open fun ormInboxRepository(
        inboxJpaRepository: InboxJpaRepository,
        integrationEventConverter: IntegrationEventConverter,
    ): InboxRepository {
        return OrmInboxRepository(
            inboxJpaRepository = inboxJpaRepository,
            integrationEventConverter = integrationEventConverter,
        )
    }

    @Bean
    open fun pollToPublishInboxMessagesJob(
        inboxJpaRepository: InboxJpaRepository,
        integrationEventPublisher: IntegrationEventPublisher,
        integrationEventSerializer: IntegrationEventSerializer,
    ) = PollToPublishInboxMessagesJob(
        inboxJpaRepository = inboxJpaRepository,
        integrationEventPublisher = integrationEventPublisher,
        integrationEventSerializer = integrationEventSerializer,
    )
}