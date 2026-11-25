package github.com.hukuta94.delivery.core.application.usecase.order

import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkFake
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryFake
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryFake
import github.com.hukuta94.delivery.core.application.port.repository.event.OutboxEventRepositoryFake
import github.com.hukuta94.delivery.core.domain.FakeDomainEvent
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.busyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedEvent
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.assignedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.completedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.createdOrder
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.core.domain.common.maxLocation
import github.com.hukuta94.delivery.core.domain.rule.impl.DispatchOrderToCourierBusinessRuleImpl
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.ints
import java.util.UUID

class AssignCourierToOrderUseCaseTest : StringSpec({

    val orderRepository = OrderRepositoryFake()
    val courierRepository = CourierRepositoryFake()
    val outboxEventRepository = OutboxEventRepositoryFake()

    fun assignUseCase(unitOfWork: UnitOfWorkPort) = AssignCourierToOrderUseCase(
        orderRepository = orderRepository,
        courierRepository = courierRepository,
        dispatchOrderToCourierBusinessRule = DispatchOrderToCourierBusinessRuleImpl(),
        unitOfWork = unitOfWork,
        outboxEventRepositoryPort = outboxEventRepository,
    )

    fun savedAssignments(): List<Pair<UUID, UUID>> = outboxEventRepository.savedEvents
        .filterIsInstance<OrderAssignedEvent>()
        .map { it.orderId to it.courierId }

    fun clearRepositories() {
        orderRepository.clear()
        courierRepository.clear()
        outboxEventRepository.clear()
    }

    val sut = assignUseCase(UnitOfWorkFake())

    beforeTest { clearRepositories() }

    "free courier takes a created order and becomes busy" {
        // Given
        val order = createdOrder()
        val courier = freeCourier()
        orderRepository.add(order)
        courierRepository.add(courier)

        // When
        sut()

        // Then
        assertSoftly {
            order.status shouldBe OrderStatus.ASSIGNED
            order.courierId shouldBe courier.id
            courier.status shouldBe CourierStatus.BUSY
        }
    }

    "assignment is saved together with the order assigned event" {
        // Given
        val order = createdOrder()
        val courier = freeCourier()
        orderRepository.add(order)
        courierRepository.add(courier)

        // When
        sut()

        // Then
        assertSoftly {
            orderRepository.updatedIds shouldContainExactly listOf(order.id)
            courierRepository.updatedIds shouldContainExactly listOf(courier.id)
            savedAssignments() shouldContainExactly listOf(order.id to courier.id)
        }
    }

    "no courier gets more than one order whatever the number of orders and couriers" {
        checkAll(Exhaustive.ints(0..6), Exhaustive.ints(0..6)) { orderCount, courierCount ->
            // Given
            clearRepositories()
            val orders = List(orderCount) { createdOrder() }
            val couriers = List(courierCount) { freeCourier() }
            orderRepository.addAll(orders)
            courierRepository.addAll(couriers)

            // When
            sut()

            // Then
            val assignedOrders = orders.filter { it.status == OrderStatus.ASSIGNED }
            val assignedCourierIds = assignedOrders.map { it.courierId }
            assertSoftly {
                assignedOrders.size shouldBe minOf(orderCount, courierCount)
                assignedCourierIds.shouldBeUnique()
                couriers.filter { it.status == CourierStatus.BUSY }
                    .map { it.id } shouldContainExactlyInAnyOrder assignedCourierIds
                savedAssignments() shouldContainExactlyInAnyOrder assignedOrders.map { it.id to it.courierId }
            }
        }
    }

    "orders left without a free courier stay created" {
        // Given
        val orders = List(3) { createdOrder() }
        val courier = freeCourier()
        orderRepository.addAll(orders)
        courierRepository.add(courier)

        // When
        sut()

        // Then
        val (assignedOrders, waitingOrders) = orders.partition { it.status == OrderStatus.ASSIGNED }
        assertSoftly {
            assignedOrders.map { it.courierId } shouldContainExactly listOf(courier.id)
            waitingOrders.map { it.status } shouldContainExactly List(2) { OrderStatus.CREATED }
            waitingOrders.map { it.courierId } shouldContainExactly listOf(null, null)
            savedAssignments().map { it.first } shouldContainExactly assignedOrders.map { it.id }
        }
    }

    "couriers left without an order stay free" {
        // Given
        val order = createdOrder()
        val couriers = List(3) { freeCourier() }
        orderRepository.add(order)
        courierRepository.addAll(couriers)

        // When
        sut()

        // Then
        val (busyCouriers, freeCouriers) = couriers.partition { it.status == CourierStatus.BUSY }
        assertSoftly {
            busyCouriers.map { it.id } shouldContainExactly listOf(order.courierId)
            freeCouriers.map { it.status } shouldContainExactly List(2) { CourierStatus.FREE }
        }
    }

    "already assigned orders and busy couriers are left as they are" {
        // Given
        val orderLocation = location(5, 5)
        val busyCourier = busyCourier(location = orderLocation) // nearest, but must not be chosen
        val freeCourier = freeCourier(location = maxLocation())
        val assignedOrder = assignedOrder(courier = busyCourier)
        val createdOrder = createdOrder(location = orderLocation)
        orderRepository.addAll(listOf(assignedOrder, createdOrder))
        courierRepository.addAll(listOf(busyCourier, freeCourier))

        // When
        sut()

        // Then
        assertSoftly {
            createdOrder.courierId shouldBe freeCourier.id
            assignedOrder.courierId shouldBe busyCourier.id
            orderRepository.updatedIds shouldContainExactly listOf(createdOrder.id)
            courierRepository.updatedIds shouldContainExactly listOf(freeCourier.id)
            savedAssignments() shouldContainExactly listOf(createdOrder.id to freeCourier.id)
        }
    }

    "nothing is assigned or saved when there are no created orders" {
        // Given
        val courier = freeCourier()
        orderRepository.addAll(listOf(assignedOrder(), completedOrder()))
        courierRepository.add(courier)

        // When
        sut()

        // Then
        assertSoftly {
            courier.status shouldBe CourierStatus.FREE
            orderRepository.updatedIds.shouldBeEmpty()
            courierRepository.updatedIds.shouldBeEmpty()
            outboxEventRepository.savedEvents.shouldBeEmpty()
        }
    }

    "nothing is assigned or saved when there are no free couriers" {
        // Given
        val orders = List(2) { createdOrder() }
        orderRepository.addAll(orders)
        courierRepository.add(busyCourier())

        // When
        sut()

        // Then
        assertSoftly {
            orders.map { it.status } shouldContainExactly List(2) { OrderStatus.CREATED }
            orderRepository.updatedIds.shouldBeEmpty()
            courierRepository.updatedIds.shouldBeEmpty()
            outboxEventRepository.savedEvents.shouldBeEmpty()
        }
    }

    "nothing is saved when there are neither orders nor couriers" {
        // Given: empty repositories

        // When
        sut()

        // Then
        assertSoftly {
            orderRepository.updatedIds.shouldBeEmpty()
            courierRepository.updatedIds.shouldBeEmpty()
            outboxEventRepository.savedEvents.shouldBeEmpty()
        }
    }

    "events raised by couriers are saved to the outbox too" {
        // Given
        val courierEvent = FakeDomainEvent()
        val courier = freeCourier().apply { raiseDomainEvent(courierEvent) }
        orderRepository.add(createdOrder())
        courierRepository.add(courier)

        // When
        sut()

        // Then
        outboxEventRepository.savedEvents shouldContain courierEvent
    }

    "nothing is dispatched outside the transaction" {
        // Given
        val transactionNotRunningWork = object : UnitOfWorkPort {
            override fun executeInTransaction(action: () -> Unit) = Unit
        }
        val sutWithoutTransaction = assignUseCase(transactionNotRunningWork)
        val order = createdOrder()
        val courier = freeCourier()
        orderRepository.add(order)
        courierRepository.add(courier)

        // When
        sutWithoutTransaction()

        // Then
        assertSoftly {
            order.status shouldBe OrderStatus.CREATED
            courier.status shouldBe CourierStatus.FREE
            orderRepository.updatedIds.shouldBeEmpty()
            courierRepository.updatedIds.shouldBeEmpty()
            outboxEventRepository.savedEvents.shouldBeEmpty()
        }
    }
})
