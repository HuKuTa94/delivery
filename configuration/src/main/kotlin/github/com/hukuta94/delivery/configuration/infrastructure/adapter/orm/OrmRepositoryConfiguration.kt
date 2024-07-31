package github.com.hukuta94.delivery.configuration.infrastructure.adapter.orm

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventDeserializer
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventPublisher
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventSerializer
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventPublisher
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventSerializer
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import github.com.hukuta94.delivery.core.application.port.repository.event.InboxEventRepositoryPort
import github.com.hukuta94.delivery.infrastructure.adapter.orm.job.PollToPublishInboxMessagesJob
import github.com.hukuta94.delivery.infrastructure.adapter.orm.job.PollToPublishOutboxMessagesJob
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.event.OrmOutboxEventRepositoryAdapter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.OrmUnitOfWorkAdapter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.event.OrmInboxEventRepositoryAdapter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.event.InboxEventJpaRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.event.OutboxEventJpaRepository
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
    open fun ormUnitOfWork(): UnitOfWorkPort {
        return OrmUnitOfWorkAdapter()
    }

    @Bean
    open fun ormOutboxRepository(
        outboxEventJpaRepository: OutboxEventJpaRepository,
        domainEventSerializer: DomainEventSerializer,
    ) = OrmOutboxEventRepositoryAdapter(
        outboxEventJpaRepository = outboxEventJpaRepository,
        domainEventSerializer = domainEventSerializer,
    )

    @Bean
    open fun pollToPublishOutboxMessagesJob(
        outboxEventJpaRepository: OutboxEventJpaRepository,
        domainEventPublisher: DomainEventPublisher,
        domainEventDeserializer: DomainEventDeserializer,
    ) = PollToPublishOutboxMessagesJob(
        outboxEventJpaRepository = outboxEventJpaRepository,
        domainEventPublisher = domainEventPublisher,
        domainEventDeserializer = domainEventDeserializer,
    )

    @Bean
    open fun ormInboxRepository(
        inboxJpaRepository: InboxEventJpaRepository,
        integrationEventSerializer: IntegrationEventSerializer,
    ): InboxEventRepositoryPort {
        return OrmInboxEventRepositoryAdapter(
            inboxJpaRepository = inboxJpaRepository,
            integrationEventSerializer = integrationEventSerializer,
        )
    }

    @Bean
    open fun pollToPublishInboxMessagesJob(
        inboxJpaRepository: InboxEventJpaRepository,
        integrationEventPublisher: IntegrationEventPublisher,
        integrationEventDeserializer: IntegrationEventDeserializer,
    ) = PollToPublishInboxMessagesJob(
        inboxJpaRepository = inboxJpaRepository,
        integrationEventPublisher = integrationEventPublisher,
        integrationEventDeserializer = integrationEventDeserializer,
    )
}