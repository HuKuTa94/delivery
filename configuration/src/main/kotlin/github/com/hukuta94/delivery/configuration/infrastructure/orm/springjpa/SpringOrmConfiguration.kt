package github.com.hukuta94.delivery.configuration.infrastructure.orm.springjpa

import github.com.hukuta94.delivery.core.application.event.ApplicationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.ApplicationEventPublisher
import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import github.com.hukuta94.delivery.infrastructure.orm.commons.InboxEventMessageRelayJob
import github.com.hukuta94.delivery.infrastructure.orm.commons.OutboxEventMessageRelayJob
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.SpringJpaUnitOfWorkAdapter
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain.CourierJpaRepository
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain.OrderJpaRepository
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain.OrmCourierRepositoryAdapter
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain.OrmOrderRepositoryAdapter
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event.SpringJpaInboxEventRepository
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event.SpringJpaOutboxEventRepository
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event.SpringJpaInboxEventRepositoryAdapter
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event.SpringJpaOutboxEventRepositoryAdapter
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@EntityScan(
    basePackages = [
        "github.com.hukuta94.delivery.infrastructure.orm.spring.model.entity"
    ]
)
@EnableJpaRepositories(
    basePackages = [
        "github.com.hukuta94.delivery.infrastructure.orm.spring.repository"
    ]
)
open class SpringOrmConfiguration {

    @Bean
    open fun courierRepository(
        courierJpaRepository: CourierJpaRepository,
        outboxRepository: SpringJpaOutboxEventRepositoryAdapter,
    ) = OrmCourierRepositoryAdapter(
        courierJpaRepository = courierJpaRepository,
        outboxRepository = outboxRepository,
    )

    @Bean
    open fun orderRepository(
        orderJpaRepository: OrderJpaRepository,
        outboxRepository: SpringJpaOutboxEventRepositoryAdapter,
    ) = OrmOrderRepositoryAdapter(
        orderJpaRepository = orderJpaRepository,
        outboxRepository = outboxRepository,
    )

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

    @Bean
    open fun outboxEventMessageRelayJob(
        eventRepository: SpringJpaOutboxEventRepositoryAdapter,
        eventDeserializer: ApplicationEventDeserializer,
        eventPublisher: ApplicationEventPublisher,
    ) = OutboxEventMessageRelayJob(
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

    @Bean
    open fun inboxEventMessageRelayJob(
        eventRepository: SpringJpaInboxEventRepositoryAdapter,
        eventDeserializer: ApplicationEventDeserializer,
        eventPublisher: ApplicationEventPublisher,
    ) = InboxEventMessageRelayJob(
        eventRepository = eventRepository,
        eventDeserializer = eventDeserializer,
        eventPublisher = eventPublisher,
    )
}
