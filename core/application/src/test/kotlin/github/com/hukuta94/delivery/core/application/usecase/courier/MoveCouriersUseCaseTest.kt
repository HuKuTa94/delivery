package github.com.hukuta94.delivery.core.application.usecase.courier

import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkFake
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryFake
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryFake
import github.com.hukuta94.delivery.core.application.port.repository.event.OutboxEventRepositoryFake
import github.com.hukuta94.delivery.core.domain.FakeDomainEvent
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Transport
import github.com.hukuta94.delivery.core.domain.aggregate.courier.busyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.Order
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedDomainEvent
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.assignedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.completedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.createdOrder
import github.com.hukuta94.delivery.core.domain.common.Location
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.core.domain.common.randomLocation
import github.com.hukuta94.delivery.core.domain.rule.impl.CompleteOrderBusinessRuleImpl
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.enum
import io.kotest.property.checkAll
import java.util.UUID
import kotlin.math.ceil

class MoveCouriersUseCaseTest : StringSpec({

    val orderRepository = OrderRepositoryFake()
    val courierRepository = CourierRepositoryFake()
    val outboxEventRepository = OutboxEventRepositoryFake()

    fun moveUseCase(unitOfWork: UnitOfWorkPort) = MoveCouriersUseCase(
        orderRepository = orderRepository,
        courierRepository = courierRepository,
        completeOrderBusinessRule = CompleteOrderBusinessRuleImpl(),
        unitOfWork = unitOfWork,
        outboxEventRepositoryPort = outboxEventRepository,
    )

    fun completedOrderIds(): List<UUID> = outboxEventRepository.savedEvents
        .filterIsInstance<OrderCompletedDomainEvent>()
        .map { it.orderId }

    fun clearRepositories() {
        orderRepository.clear()
        courierRepository.clear()
        outboxEventRepository.clear()
    }

    val sut = moveUseCase(UnitOfWorkFake())

    /**
     * Runs the use case tick by tick, like the scheduler does, until the [order] is completed.
     * Returns the number of ticks it took, or `null` if the order is still not completed after [TICK_LIMIT] ticks.
     */
    fun ticksUntilCompleted(order: Order): Int? = (1..TICK_LIMIT).firstOrNull {
        sut()
        order.status == OrderStatus.COMPLETED
    }

    beforeTest { clearRepositories() }

    "courier one step away from the order delivers it and becomes free" {
        // Given
        val courier = busyCourier(location = location(5, 5))
        val order = assignedOrder(
            location = location(5, 6),
            courier = courier,
        )
        orderRepository.add(order)
        courierRepository.add(courier)

        // When
        sut()

        // Then
        assertSoftly {
            courier.location shouldBe order.location
            courier.status shouldBe CourierStatus.FREE
            order.status shouldBe OrderStatus.COMPLETED
        }
    }

    "delivery is saved together with the order completed event" {
        // Given
        val courier = busyCourier(location = location(5, 5))
        val order = assignedOrder(
            location = location(5, 6),
            courier = courier,
        )
        orderRepository.add(order)
        courierRepository.add(courier)

        // When
        sut()

        // Then
        assertSoftly {
            orderRepository.updatedIds shouldContainExactly listOf(order.id)
            courierRepository.updatedIds shouldContainExactly listOf(courier.id)
            completedOrderIds() shouldContainExactly listOf(order.id)
        }
    }

    "courier still on the way moves one step closer and keeps the order" {
        // Given
        val courier = busyCourier(location = location(1, 1))
        val order = assignedOrder(
            location = location(1, 9),
            courier = courier,
        )
        orderRepository.add(order)
        courierRepository.add(courier)

        // When
        sut()

        // Then
        assertSoftly {
            courier.location shouldBe location(1, 2)
            courier.status shouldBe CourierStatus.BUSY
            order.status shouldBe OrderStatus.ASSIGNED
            courierRepository.updatedIds shouldContainExactly listOf(courier.id)
            orderRepository.updatedIds.shouldBeEmpty()
            completedOrderIds().shouldBeEmpty()
        }
    }

    "courier already standing at the order location delivers it without moving" {
        // Given
        val orderLocation = location(5, 5)
        val courier = busyCourier(location = orderLocation)
        val order = assignedOrder(
            location = orderLocation,
            courier = courier,
        )
        orderRepository.add(order)
        courierRepository.add(courier)

        // When
        sut()

        // Then
        assertSoftly {
            courier.location shouldBe orderLocation
            courier.status shouldBe CourierStatus.FREE
            order.status shouldBe OrderStatus.COMPLETED
            completedOrderIds() shouldContainExactly listOf(order.id)
        }
    }

    "courier delivers the order in as many ticks as its transport needs to cover the distance" {
        checkAll(ANY_LOCATION, ANY_LOCATION, Arb.enum<Transport>()) { courierLocation, orderLocation, transport ->
            // Given
            clearRepositories()
            val courier = busyCourier(
                location = courierLocation,
                transport = transport,
            )
            val order = assignedOrder(
                location = orderLocation,
                courier = courier,
            )
            orderRepository.add(order)
            courierRepository.add(courier)

            // When
            val ticks = ticksUntilCompleted(order)

            // Then
            val distance = courierLocation.distanceTo(orderLocation)
            // a courier already at the order location still needs one tick to hand it over
            val expectedTicks = maxOf(1, ceil(distance.toDouble() / transport.speed).toInt())
            assertSoftly {
                ticks shouldBe expectedTicks
                courier.location shouldBe orderLocation
                courier.status shouldBe CourierStatus.FREE
                completedOrderIds() shouldContainExactly listOf(order.id)
            }
        }
    }

    "arriving and travelling couriers are handled in the same tick" {
        // Given
        val arrivingCourier = busyCourier(location = location(5, 5))
        val arrivingOrder = assignedOrder(
            location = location(5, 6),
            courier = arrivingCourier,
        )
        val travellingCourier = busyCourier(location = location(1, 1))
        val travellingOrder = assignedOrder(
            location = location(1, 9),
            courier = travellingCourier,
        )
        orderRepository.addAll(listOf(arrivingOrder, travellingOrder))
        courierRepository.addAll(listOf(arrivingCourier, travellingCourier))

        // When
        sut()

        // Then
        assertSoftly {
            arrivingOrder.status shouldBe OrderStatus.COMPLETED
            arrivingCourier.status shouldBe CourierStatus.FREE
            travellingOrder.status shouldBe OrderStatus.ASSIGNED
            travellingCourier.status shouldBe CourierStatus.BUSY
            travellingCourier.location shouldBe location(1, 2)
            courierRepository.updatedIds shouldContainExactlyInAnyOrder listOf(
                arrivingCourier.id,
                travellingCourier.id,
            )
            orderRepository.updatedIds shouldContainExactly listOf(arrivingOrder.id)
            completedOrderIds() shouldContainExactly listOf(arrivingOrder.id)
        }
    }

    "free couriers, created and completed orders are left alone" {
        // Given
        val freeCourierLocation = location(1, 1)
        val freeCourier = freeCourier(location = freeCourierLocation)
        val createdOrder = createdOrder()
        val completedOrder = completedOrder(courier = freeCourier)
        val deliveringCourier = busyCourier(location = location(5, 5))
        val deliveringOrder = assignedOrder(
            location = location(5, 6),
            courier = deliveringCourier,
        )
        orderRepository.addAll(listOf(createdOrder, completedOrder, deliveringOrder))
        courierRepository.addAll(listOf(freeCourier, deliveringCourier))

        // When
        sut()

        // Then
        assertSoftly {
            freeCourier.location shouldBe freeCourierLocation
            createdOrder.status shouldBe OrderStatus.CREATED
            courierRepository.updatedIds shouldContainExactly listOf(deliveringCourier.id)
            orderRepository.updatedIds shouldContainExactly listOf(deliveringOrder.id)
            completedOrderIds() shouldContainExactly listOf(deliveringOrder.id)
        }
    }

    "busy courier without an assigned order stays where it is" {
        // Given
        val idleCourierLocation = location(1, 1)
        val idleCourier = busyCourier(location = idleCourierLocation)
        val deliveringCourier = busyCourier(location = location(5, 5))
        val order = assignedOrder(
            location = location(5, 6),
            courier = deliveringCourier,
        )
        orderRepository.add(order)
        courierRepository.addAll(listOf(idleCourier, deliveringCourier))

        // When
        sut()

        // Then
        assertSoftly {
            idleCourier.location shouldBe idleCourierLocation
            idleCourier.status shouldBe CourierStatus.BUSY
            courierRepository.updatedIds shouldContainExactly listOf(deliveringCourier.id)
        }
    }

    "nothing is moved or saved when there are no busy couriers" {
        // Given
        val courierLocation = location(1, 1)
        val courier = freeCourier(location = courierLocation)
        orderRepository.add(createdOrder())
        courierRepository.add(courier)

        // When
        sut()

        // Then
        assertSoftly {
            courier.location shouldBe courierLocation
            orderRepository.updatedIds.shouldBeEmpty()
            courierRepository.updatedIds.shouldBeEmpty()
            outboxEventRepository.savedEvents.shouldBeEmpty()
        }
    }

    "nothing is moved or saved when there are no assigned orders" {
        // Given
        val courierLocation = location(1, 1)
        val courier = busyCourier(location = courierLocation)
        orderRepository.addAll(listOf(createdOrder(), completedOrder()))
        courierRepository.add(courier)

        // When
        sut()

        // Then
        assertSoftly {
            courier.location shouldBe courierLocation
            orderRepository.updatedIds.shouldBeEmpty()
            courierRepository.updatedIds.shouldBeEmpty()
            outboxEventRepository.savedEvents.shouldBeEmpty()
        }
    }

    "events raised by moving couriers are saved to the outbox too" {
        // Given
        val courierEvent = FakeDomainEvent()
        val courier = busyCourier(location = location(1, 1)).apply { raiseDomainEvent(courierEvent) }
        val order = assignedOrder(
            location = location(1, 9),
            courier = courier,
        )
        orderRepository.add(order)
        courierRepository.add(courier)

        // When
        sut()

        // Then
        outboxEventRepository.savedEvents shouldContain courierEvent
    }

    "assigned order whose courier is not busy is skipped and other couriers keep delivering" {
        // Given
        val courier = busyCourier(location = location(5, 5))
        val order = assignedOrder(
            location = location(5, 6),
            courier = courier,
        )
        val freeCourierLocation = location(1, 1)
        val freeCourier = freeCourier(location = freeCourierLocation)
        val orderOfFreeCourier = assignedOrder(
            location = location(1, 9),
            courier = freeCourier,
        )
        orderRepository.addAll(listOf(order, orderOfFreeCourier))
        courierRepository.addAll(listOf(courier, freeCourier))

        // When
        sut()

        // Then
        assertSoftly {
            order.status shouldBe OrderStatus.COMPLETED
            courier.status shouldBe CourierStatus.FREE
            orderOfFreeCourier.status shouldBe OrderStatus.ASSIGNED
            freeCourier.location shouldBe freeCourierLocation
            courierRepository.updatedIds shouldContainExactly listOf(courier.id)
            orderRepository.updatedIds shouldContainExactly listOf(order.id)
            completedOrderIds() shouldContainExactly listOf(order.id)
        }
    }

    "nothing is moved outside the transaction" {
        // Given
        val transactionNotRunningWork = object : UnitOfWorkPort {
            override fun executeInTransaction(action: () -> Unit) = Unit
        }
        val sutWithoutTransaction = moveUseCase(transactionNotRunningWork)
        val courierLocation = location(5, 5)
        val courier = busyCourier(location = courierLocation)
        val order = assignedOrder(
            location = location(5, 6),
            courier = courier,
        )
        orderRepository.add(order)
        courierRepository.add(courier)

        // When
        sutWithoutTransaction()

        // Then
        assertSoftly {
            courier.location shouldBe courierLocation
            courier.status shouldBe CourierStatus.BUSY
            order.status shouldBe OrderStatus.ASSIGNED
            orderRepository.updatedIds.shouldBeEmpty()
            courierRepository.updatedIds.shouldBeEmpty()
            outboxEventRepository.savedEvents.shouldBeEmpty()
        }
    }
}) {
    companion object {
        /** Guard against an endless loop, well above the longest delivery (corner to corner on foot: 18 ticks). */
        private const val TICK_LIMIT = 100
        private val ANY_LOCATION: Arb<Location> = arbitrary { randomLocation(random = it.random) }
    }
}
