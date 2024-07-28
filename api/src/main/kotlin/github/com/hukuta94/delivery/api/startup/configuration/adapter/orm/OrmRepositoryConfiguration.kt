package github.com.hukuta94.delivery.api.startup.configuration.adapter.orm

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventDeserializer
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventPublisher
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventSerializer
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventPublisher
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventSerializer
import github.com.hukuta94.delivery.core.port.UnitOfWork
import github.com.hukuta94.delivery.core.port.repository.box.InboxRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.job.PollToPublishInboxMessagesJob
import github.com.hukuta94.delivery.infrastructure.adapter.orm.job.PollToPublishOutboxMessagesJob
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
open class OrmRepositoryConfiguration {

    @Bean
    open fun ormUnitOfWork(): UnitOfWork {
        return OrmUnitOfWork()
    }

    @Bean
    open fun ormOutboxRepository(
        outboxJpaRepository: OutboxJpaRepository,
        domainEventSerializer: DomainEventSerializer,
    ) = OrmOutboxRepository(
        outboxJpaRepository = outboxJpaRepository,
        domainEventSerializer = domainEventSerializer,
    )

    @Bean
    open fun pollToPublishOutboxMessagesJob(
        outboxJpaRepository: OutboxJpaRepository,
        domainEventPublisher: DomainEventPublisher,
        domainEventDeserializer: DomainEventDeserializer,
    ) = PollToPublishOutboxMessagesJob(
        outboxJpaRepository = outboxJpaRepository,
        domainEventPublisher = domainEventPublisher,
        domainEventDeserializer = domainEventDeserializer,
    )

    @Bean
    open fun ormInboxRepository(
        inboxJpaRepository: InboxJpaRepository,
        integrationEventSerializer: IntegrationEventSerializer,
    ): InboxRepository {
        return OrmInboxRepository(
            inboxJpaRepository = inboxJpaRepository,
            integrationEventSerializer = integrationEventSerializer,
        )
    }

    @Bean
    open fun pollToPublishInboxMessagesJob(
        inboxJpaRepository: InboxJpaRepository,
        integrationEventPublisher: IntegrationEventPublisher,
        integrationEventDeserializer: IntegrationEventDeserializer,
    ) = PollToPublishInboxMessagesJob(
        inboxJpaRepository = inboxJpaRepository,
        integrationEventPublisher = integrationEventPublisher,
        integrationEventDeserializer = integrationEventDeserializer,
    )
}