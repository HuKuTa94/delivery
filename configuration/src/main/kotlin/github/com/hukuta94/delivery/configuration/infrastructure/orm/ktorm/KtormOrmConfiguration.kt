package github.com.hukuta94.delivery.configuration.infrastructure.orm.ktorm

import github.com.hukuta94.delivery.core.application.event.ApplicationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.ApplicationEventPublisher
import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryPort
import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.application.query.order.GetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.KtormUnitOfWork
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.query.KtormGetBusyCouriersQuery
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.query.KtormGetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.KtormCourierRepository
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.KtormInboxEventRepository
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.KtormOrderRepository
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.KtormOutboxEventRepository
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.inoutbox.SpringInboxEventMessageRelayJob
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.inoutbox.SpringOutboxEventMessageRelayJob
import org.ktorm.database.Database
import org.ktorm.support.postgresql.PostgreSqlDialect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
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
        eventSerializer: ApplicationEventSerializer,
    ) = KtormInboxEventRepository(
        database = database,
        eventSerializer = eventSerializer,
    )

    @Bean
    open fun ktormOutboxEventMessageRelayRepository(
        database: Database,
        eventSerializer: ApplicationEventSerializer,
    ) = KtormOutboxEventRepository(
        database = database,
        eventSerializer = eventSerializer,
    )

    //TODO вынести общую логику в модуль orm:commons
    @Bean
    open fun springOutboxEventMessageRelayJob(
        eventRepository: KtormOutboxEventRepository,
        eventDeserializer: ApplicationEventDeserializer,
        eventPublisher: ApplicationEventPublisher,
    ) = SpringOutboxEventMessageRelayJob(
        eventRepository = eventRepository,
        eventDeserializer = eventDeserializer,
        eventPublisher = eventPublisher,
    )

    //TODO вынести общую логику в модуль orm:commons
    @Bean
    open fun springInboxEventMessageRelayJob(
        eventRepository: KtormInboxEventRepository,
        eventDeserializer: ApplicationEventDeserializer,
        eventPublisher: ApplicationEventPublisher,
    ) = SpringInboxEventMessageRelayJob(
        eventRepository = eventRepository,
        eventDeserializer = eventDeserializer,
        eventPublisher = eventPublisher,
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