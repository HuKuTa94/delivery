package github.com.hukuta94.delivery.configuration.infrastructure.orm

import github.com.hukuta94.delivery.core.application.event.ApplicationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.ApplicationEventPublisher
import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import github.com.hukuta94.delivery.core.application.port.repository.event.InboxEventRepositoryPort
import github.com.hukuta94.delivery.infrastructure.orm.job.PublishInboxMessagesJob
import github.com.hukuta94.delivery.infrastructure.orm.job.PublishOutboxMessagesJob
import github.com.hukuta94.delivery.infrastructure.orm.repository.OrmUnitOfWorkAdapter
import github.com.hukuta94.delivery.infrastructure.orm.repository.event.InboxEventJpaRepository
import github.com.hukuta94.delivery.infrastructure.orm.repository.event.OrmInboxEventRepositoryAdapter
import github.com.hukuta94.delivery.infrastructure.orm.repository.event.OrmOutboxEventRepositoryAdapter
import github.com.hukuta94.delivery.infrastructure.orm.repository.event.OutboxEventJpaRepository
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
        "github.com.hukuta94.delivery.infrastructure.orm.model.entity"
    ]
)
@EnableJpaRepositories(
    basePackages = [
        "github.com.hukuta94.delivery.infrastructure.orm.repository"
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
        domainEventSerializer: ApplicationEventSerializer,
    ) = OrmOutboxEventRepositoryAdapter(
        outboxEventJpaRepository = outboxEventJpaRepository,
        eventSerializer = domainEventSerializer,
    )

    @Bean
    open fun pollToPublishOutboxMessagesJob(
        eventJpaRepository: OutboxEventJpaRepository,
        eventDeserializer: ApplicationEventDeserializer,
        eventPublisher: ApplicationEventPublisher,
    ) = PublishOutboxMessagesJob(
        eventJpaRepository = eventJpaRepository,
        eventDeserializer = eventDeserializer,
        eventPublisher = eventPublisher,
    )

    @Bean
    open fun ormInboxRepository(
        inboxJpaRepository: InboxEventJpaRepository,
        integrationEventSerializer: ApplicationEventSerializer,
    ): InboxEventRepositoryPort {
        return OrmInboxEventRepositoryAdapter(
            inboxJpaRepository = inboxJpaRepository,
            eventSerializer = integrationEventSerializer,
        )
    }

    @Bean
    open fun pollToPublishInboxMessagesJob(
        eventJpaRepository: InboxEventJpaRepository,
        eventDeserializer: ApplicationEventDeserializer,
        eventPublisher: ApplicationEventPublisher,
    ) = PublishInboxMessagesJob(
        eventJpaRepository = eventJpaRepository,
        eventDeserializer = eventDeserializer,
        eventPublisher = eventPublisher,
    )
}