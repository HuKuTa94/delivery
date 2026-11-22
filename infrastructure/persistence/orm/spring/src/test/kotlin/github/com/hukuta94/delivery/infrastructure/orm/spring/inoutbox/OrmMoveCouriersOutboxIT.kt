package github.com.hukuta94.delivery.infrastructure.orm.spring.inoutbox

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkFake
import github.com.hukuta94.delivery.core.application.usecase.courier.impl.MoveCouriersUseCaseImpl
import github.com.hukuta94.delivery.core.domain.aggregate.courier.busyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedDomainEvent
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.assignedOrder
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.core.domain.rule.impl.CompleteOrderBusinessRuleImpl
import github.com.hukuta94.delivery.infrastructure.orm.spring.integration.SpringJpaIntegrationSpec
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain.CourierJpaRepository
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain.OrderJpaRepository
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain.OrmCourierRepositoryAdapter
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain.OrmOrderRepositoryAdapter
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event.SpringJpaOutboxEventRepository
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event.SpringJpaOutboxEventRepositoryAdapter
import io.kotest.matchers.shouldBe
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

/**
 * Verifies A2 under Spring JPA: completing an order through the use case collects the
 * OrderCompletedDomainEvent and persists it into the outbox (parity with the Ktorm implementation).
 */
internal class OrmMoveCouriersOutboxIT : SpringJpaIntegrationSpec() {
    init {
        val orderAdapter = OrmOrderRepositoryAdapter(context.getBean(OrderJpaRepository::class.java))
        val courierAdapter = OrmCourierRepositoryAdapter(context.getBean(CourierJpaRepository::class.java))
        val outboxAdapter = SpringJpaOutboxEventRepositoryAdapter(
            context.getBean(SpringJpaOutboxEventRepository::class.java),
            ApplicationEventSerializer(),
        )
        val transaction = TransactionTemplate(context.getBean(PlatformTransactionManager::class.java))

        "MoveCouriers completes an order and writes its OrderCompletedDomainEvent to the outbox" {
            val useCase = MoveCouriersUseCaseImpl(
                orderAdapter,
                courierAdapter,
                CompleteOrderBusinessRuleImpl(),
                UnitOfWorkFake(),
                outboxAdapter,
            )

            val deliveryLocation = location(5, 5)
            val courier = busyCourier(location = deliveryLocation)
            courierAdapter.add(courier)
            val order = assignedOrder(location = deliveryLocation, courier = courier)
            orderAdapter.add(order)

            useCase.execute()

            orderAdapter.getById(order.id).status shouldBe OrderStatus.COMPLETED
            val outboxMessages = transaction.execute {
                outboxAdapter.findMessagesInStatuses(setOf(BoxEventMessageStatus.TO_BE_PROCESSED), batchSize = 50)
            }
            outboxMessages?.mapNotNull { it.eventType } shouldBe listOf(OrderCompletedDomainEvent::class.java)
        }
    }
}
