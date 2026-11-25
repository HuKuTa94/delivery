package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.domain

import github.com.hukuta94.delivery.core.application.event.EventSerializer
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkFake
import github.com.hukuta94.delivery.core.application.usecase.courier.MoveCouriersUseCase
import github.com.hukuta94.delivery.core.domain.aggregate.courier.busyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedEvent
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.assignedOrder
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.core.domain.rule.impl.CompleteOrderBusinessRuleImpl
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.integration.KtormIntegrationSpec
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.event.KtormOutboxEventRepository
import io.kotest.matchers.shouldBe

/**
 * Verifies A2 under Ktorm: completing an order through the use case collects the
 * OrderCompletedDomainEvent and persists it into the outbox (this was the event lost before A2).
 */
internal class KtormMoveCouriersOutboxIT : KtormIntegrationSpec() {
    init {
        "MoveCouriers completes an order and writes its OrderCompletedDomainEvent to the outbox" {
            val orderRepository = KtormOrderRepository(database)
            val courierRepository = KtormCourierRepository(database)
            val outboxRepository = KtormOutboxEventRepository(database, EventSerializer())
            val useCase = MoveCouriersUseCase(
                orderRepository,
                courierRepository,
                CompleteOrderBusinessRuleImpl(),
                UnitOfWorkFake(),
                outboxRepository,
            )

            val deliveryLocation = location(5, 5)
            val courier = busyCourier(
                location = deliveryLocation,
            )
            courierRepository.add(courier)
            val order = assignedOrder(
                location = deliveryLocation,
                courier = courier,
            )
            orderRepository.add(order)

            useCase()

            orderRepository.getById(order.id).status shouldBe OrderStatus.COMPLETED
            val outboxMessages = outboxRepository.findMessagesInStatuses(
                statuses = setOf(BoxEventMessageStatus.TO_BE_PROCESSED),
                batchSize = 50,
            )
            outboxMessages.map { it.eventType } shouldBe listOf(OrderCompletedEvent::class.java)
        }
    }
}
