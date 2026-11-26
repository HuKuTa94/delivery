package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.domain

import github.com.hukuta94.delivery.core.application.event.EventSerializer
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import github.com.hukuta94.delivery.core.application.usecase.courier.MoveCouriersUseCase
import github.com.hukuta94.delivery.core.application.usecase.order.AssignCourierToOrderUseCase
import github.com.hukuta94.delivery.core.domain.aggregate.courier.busyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.assignedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.createdOrder
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.core.domain.rule.impl.CompleteOrderBusinessRuleImpl
import github.com.hukuta94.delivery.core.domain.rule.impl.DispatchOrderToCourierBusinessRuleImpl
import github.com.hukuta94.delivery.infrastructure.orm.commons.integration.PostgresTestContainer
import github.com.hukuta94.delivery.infrastructure.orm.commons.integration.concurrency.StartGate
import github.com.hukuta94.delivery.infrastructure.orm.commons.integration.concurrency.failuresByType
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.KtormTransactionTestConfig
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.event.KtormOutboxEventRepository
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.ktorm.database.Database
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

/**
 * Order assignment and courier movement run on schedulers of their own, so at the same time. Both read and save
 * couriers and orders with full-row saves and no optimistic locking, so they are safe only while they change
 * different rows: assignment takes free couriers and created orders, movement takes busy couriers and assigned ones.
 * A save of one of them from a stale read shows up as a busy courier without an assigned order, or the other way.
 *
 * Ktorm's `update` writes every row it is given, changed or not, so saving rows of the other job from a stale read
 * overwrites its changes: an assignment that also saved busy couriers broke this test (unlike under JPA).
 */
internal class KtormAssignmentAndMovementRaceIT : StringSpec() {

    // Transactions as in the application: Ktorm's connectWithSpringSupport and the proxied KtormUnitOfWork
    // (a fake unit of work would commit every repository call on its own)
    private val context = AnnotationConfigApplicationContext(KtormTransactionTestConfig::class.java)

    init {
        beforeTest { PostgresTestContainer.cleanData() }
        afterSpec { context.close() }

        val database = context.getBean(Database::class.java)
        val unitOfWork = context.getBean(UnitOfWorkPort::class.java)
        val orderRepository = KtormOrderRepository(database)
        val courierRepository = KtormCourierRepository(database)
        val outboxRepository = KtormOutboxEventRepository(
            database = database,
            eventSerializer = EventSerializer(),
        )

        val assignCouriers = AssignCourierToOrderUseCase(
            orderRepository = orderRepository,
            courierRepository = courierRepository,
            dispatchOrderToCourierBusinessRule = DispatchOrderToCourierBusinessRuleImpl(),
            unitOfWork = unitOfWork,
            outboxEventRepositoryPort = outboxRepository,
        )
        val moveCouriers = MoveCouriersUseCase(
            orderRepository = orderRepository,
            courierRepository = courierRepository,
            completeOrderBusinessRule = CompleteOrderBusinessRuleImpl(),
            unitOfWork = unitOfWork,
            outboxEventRepositoryPort = outboxRepository,
        )

        // Adds couriers one step from their orders, which movement completes and frees, and as many created orders,
        // which assignment gives to free couriers, then runs both use cases at the same moment
        fun round(): Round {
            val deliveries = List(COURIERS_PER_ROUND) {
                val courier = busyCourier(location = location(5, 5))
                val order = assignedOrder(
                    location = location(5, 6),
                    courier = courier,
                )
                courierRepository.add(courier)
                orderRepository.add(order)
                order
            }
            repeat(COURIERS_PER_ROUND) { orderRepository.add(createdOrder()) }
            val outcomes = StartGate.run(
                threads = 2,
                timeout = 10.seconds,
            ) { thread ->
                if (thread == 0) assignCouriers() else moveCouriers()
            }
            return Round(
                deliveredOrderIds = deliveries.map { it.id },
                outcomes = outcomes,
            )
        }

        "assignment and movement at the same time leave every busy courier with exactly one assigned order" {
            // Given: every round adds its own deliveries and created orders, see round()

            // When
            val rounds = List(ROUNDS) { round() }

            // Then
            assertSoftly {
                rounds.flatMap { it.outcomes }.failuresByType().shouldBeEmpty()
                orderRepository.getAllAssigned().map { it.courierId } shouldContainExactlyInAnyOrder
                    courierRepository.getAllBusy().map { it.id }
                rounds.flatMap { it.deliveredOrderIds }
                    .map { orderRepository.getById(it).status }
                    .toSet() shouldBe setOf(OrderStatus.COMPLETED)
                // assignment did run: freed couriers took created orders
                orderRepository.getAllCreated().size shouldBeLessThan ROUNDS * COURIERS_PER_ROUND
            }
        }
    }

    private class Round(
        val deliveredOrderIds: List<UUID>,
        val outcomes: List<Result<Unit>>,
    )

    private companion object {
        const val ROUNDS = 50
        const val COURIERS_PER_ROUND = 5
    }
}
