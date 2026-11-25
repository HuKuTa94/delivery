package github.com.hukuta94.delivery.configuration.infrastructure.orm.ktorm

import github.com.hukuta94.delivery.configuration.infrastructure.RelayProperties
import github.com.hukuta94.delivery.core.application.event.EventDeserializer
import github.com.hukuta94.delivery.core.application.event.EventPublisher
import github.com.hukuta94.delivery.core.application.event.EventSerializer
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryPort
import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.application.query.order.GetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.infrastructure.orm.commons.InboxEventMessageRelayJob
import github.com.hukuta94.delivery.infrastructure.orm.commons.OutboxEventMessageRelayJob
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.KtormUnitOfWork
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.query.KtormGetBusyCouriersQuery
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.query.KtormGetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.domain.KtormCourierRepository
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.event.KtormInboxEventRepository
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.domain.KtormOrderRepository
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.event.KtormOutboxEventRepository
import org.ktorm.database.Database
import org.ktorm.support.postgresql.PostgreSqlDialect
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
@ConditionalOnProperty(
    prefix = "delivery",
    name = ["orm"],
    havingValue = "ktorm",
)
open class KtormOrmConfiguration {

    @Bean
    open fun ktormDatabase(dataSource: DataSource) = Database.connectWithSpringSupport(
        dataSource = dataSource,
        dialect = PostgreSqlDialect()
    )

    @Bean
    open fun ktormUnitOfWork(): UnitOfWorkPort = KtormUnitOfWork()

    @Bean
    open fun ktormCourierRepository(
        database: Database,
    ): CourierRepositoryPort = KtormCourierRepository(
        database = database,
    )

    @Bean
    open fun ktormOrderRepository(
        database: Database,
    ): OrderRepositoryPort = KtormOrderRepository(
        database = database,
    )

    @Bean
    open fun ktormInboxEventMessageRelayRepository(
        database: Database,
        eventSerializer: EventSerializer,
    ) = KtormInboxEventRepository(
        database = database,
        eventSerializer = eventSerializer,
    )

    @Bean
    open fun ktormOutboxEventMessageRelayRepository(
        database: Database,
        eventSerializer: EventSerializer,
    ) = KtormOutboxEventRepository(
        database = database,
        eventSerializer = eventSerializer,
    )

    @Bean
    open fun ktormOutboxEventMessageRelayJob(
        eventRepository: KtormOutboxEventRepository,
        eventDeserializer: EventDeserializer,
        eventPublisher: EventPublisher,
        relayProperties: RelayProperties,
    ) = OutboxEventMessageRelayJob(
        eventRepository = eventRepository,
        eventDeserializer = eventDeserializer,
        eventPublisher = eventPublisher,
        batchSize = relayProperties.batchSize,
    )

    @Bean
    open fun ktormInboxEventMessageRelayJob(
        eventRepository: KtormInboxEventRepository,
        eventDeserializer: EventDeserializer,
        eventPublisher: EventPublisher,
        relayProperties: RelayProperties,
    ) = InboxEventMessageRelayJob(
        eventRepository = eventRepository,
        eventDeserializer = eventDeserializer,
        eventPublisher = eventPublisher,
        batchSize = relayProperties.batchSize,
    )

    @Bean
    open fun ktormGetBusyCouriersQuery(
        database: Database,
    ): GetBusyCouriersQuery = KtormGetBusyCouriersQuery(
        database = database,
    )

    @Bean
    open fun ktormGetNotCompletedOrdersQuery(
        database: Database,
    ): GetNotCompletedOrdersQuery = KtormGetNotCompletedOrdersQuery(
        database = database,
    )
}
