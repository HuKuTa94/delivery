package github.com.hukuta94.delivery.configuration.infrastructure.orm

import github.com.hukuta94.delivery.core.application.event.ApplicationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.ApplicationEventPublisher
import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.inoutbox.SpringInboxEventMessageRelayJob
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.inoutbox.SpringOutboxEventMessageRelayJob
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.repository.SpringJpaUnitOfWorkAdapter
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.repository.event.SpringJpaInboxEventRepository
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.repository.event.SpringJpaOutboxEventRepository
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.repository.event.SpringJpaInboxEventRepositoryAdapter
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.repository.event.SpringJpaOutboxEventRepositoryAdapter
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
        "github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.entity"
    ]
)
@EnableJpaRepositories(
    basePackages = [
        "github.com.hukuta94.delivery.infrastructure.orm.springjpa.repository"
    ]
)
@Import(
    SpringOrderRepositoryConfiguration::class,
    SpringJpaCourierRepositoryConfiguration::class,
)
open class SpringJpaRepositoryConfiguration {

    @Bean
    open fun springJpaUnitOfWork(): UnitOfWorkPort {
        return SpringJpaUnitOfWorkAdapter()
    }

    @Bean
    open fun springJpaOutboxRepository(
        outboxEventJpaRepository: SpringJpaOutboxEventRepository,
        domainEventSerializer: ApplicationEventSerializer,
    ) = SpringJpaOutboxEventRepositoryAdapter(
        outboxEventJpaRepository = outboxEventJpaRepository,
        eventSerializer = domainEventSerializer,
    )

    //TODO вынести общую логику в модуль orm:commons
    @Bean
    open fun springOutboxEventMessageRelayJob(
        eventRepository: SpringJpaOutboxEventRepositoryAdapter,
        eventDeserializer: ApplicationEventDeserializer,
        eventPublisher: ApplicationEventPublisher,
    ) = SpringOutboxEventMessageRelayJob(
        eventRepository = eventRepository,
        eventDeserializer = eventDeserializer,
        eventPublisher = eventPublisher,
    )

    @Bean
    open fun springJpaInboxRepository(
        inboxJpaRepository: SpringJpaInboxEventRepository,
        integrationEventSerializer: ApplicationEventSerializer,
    ) = SpringJpaInboxEventRepositoryAdapter(
        inboxJpaRepository = inboxJpaRepository,
        eventSerializer = integrationEventSerializer,
    )

    //TODO вынести общую логику в модуль orm:commons
    @Bean
    open fun springInboxEventMessageRelayJob(
        eventRepository: SpringJpaInboxEventRepositoryAdapter,
        eventDeserializer: ApplicationEventDeserializer,
        eventPublisher: ApplicationEventPublisher,
    ) = SpringInboxEventMessageRelayJob(
        eventRepository = eventRepository,
        eventDeserializer = eventDeserializer,
        eventPublisher = eventPublisher,
    )
}