package github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.assignedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.completedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.createdOrder
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.infrastructure.orm.commons.integration.concurrency.StartGate
import github.com.hukuta94.delivery.infrastructure.orm.commons.integration.concurrency.failuresByType
import github.com.hukuta94.delivery.infrastructure.orm.spring.integration.SpringJpaIntegrationSpec
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

/**
 * An order is created from an inbox event, and the inbox can relay the same basket again, also from several relay
 * workers at the same moment. Adding an order that is already stored must keep the stored one and fail no caller.
 * Races depend on timing: each race runs [ROUNDS] rounds at the start gate, one fresh order id per round, and the
 * checks run once at the end.
 */
internal class OrmOrderRepositoryAdapterIT : SpringJpaIntegrationSpec() {
    init {
        val orderJpaRepository = context.getBean(OrderJpaRepository::class.java)
        val orderAdapter = OrmOrderRepositoryAdapter(orderJpaRepository)
        val courierAdapter = OrmCourierRepositoryAdapter(context.getBean(CourierJpaRepository::class.java))
        val sut = orderAdapter

        "add then getById round-trips an order" {
            val order = createdOrder(location = location(2, 3))

            orderAdapter.add(order)

            val loaded = orderAdapter.getById(order.id)
            loaded.id shouldBe order.id
            loaded.location shouldBe order.location
            loaded.status shouldBe OrderStatus.CREATED
            loaded.courierId shouldBe null
        }

        "getAllNotCompleted returns CREATED and ASSIGNED, excludes COMPLETED" {
            val courier = freeCourier()
            courierAdapter.add(courier)

            val created = createdOrder(location = location(1, 1))
            val assigned = assignedOrder(location = location(2, 2), courier = courier)
            val completed = completedOrder(location = location(3, 3), courier = courier)
            orderAdapter.add(created)
            orderAdapter.add(assigned)
            orderAdapter.add(completed)

            val notCompletedIds = orderAdapter.getAllNotCompleted().map { it.id }

            notCompletedIds shouldContainExactlyInAnyOrder listOf(created.id, assigned.id)
        }

        "a new order is reported as added" {
            // When
            val added = sut.add(createdOrder())

            // Then
            added shouldBe true
        }

        "an order added again is reported as not added and stored once" {
            // Given
            val order = createdOrder()
            sut.add(order)

            // When
            val readd = runCatching { sut.add(createdOrder(id = order.id, location = order.location)) }

            // Then
            assertSoftly {
                readd shouldBe Result.success(false)
                orderJpaRepository.count() shouldBe 1
            }
        }

        "an order added again keeps the state it has moved to since" {
            // Given
            val courier = freeCourier()
            courierAdapter.add(courier)
            val stored = assignedOrder(
                location = location(4, 5),
                courier = courier,
            )
            sut.add(stored)

            // When
            val readd = runCatching { sut.add(createdOrder(id = stored.id, location = location(9, 9))) }

            // Then
            val loaded = sut.getById(stored.id)
            assertSoftly {
                readd shouldBe Result.success(false)
                loaded.status shouldBe OrderStatus.ASSIGNED
                loaded.courierId shouldBe courier.id
                loaded.location shouldBe location(4, 5)
            }
        }

        "an order added by a few callers at once is stored once, added by one of them and fails none of them" {
            // Given
            val orderIds = List(ROUNDS) { UUID.randomUUID() }

            // When
            val outcomes = orderIds.flatMap { orderId ->
                addAtOnce { sut.add(createdOrder(id = orderId, location = location(1, 1))) }
            }

            // Then
            assertSoftly {
                orderJpaRepository.count() shouldBe ROUNDS.toLong()
                outcomes.count { it.getOrNull() == true } shouldBe ROUNDS
                outcomes.failuresByType().shouldBeEmpty()
            }
        }

        "distinct orders added at once are all stored" {
            // Given
            val rounds = List(ROUNDS) {
                List(THREADS) {
                    createdOrder()
                }
            }

            // When
            val outcomes = rounds.flatMap { orders ->
                addAtOnce { adder -> sut.add(orders[adder]) }
            }

            // Then
            assertSoftly {
                orderJpaRepository.count() shouldBe (ROUNDS * THREADS).toLong()
                outcomes.count { it.getOrNull() == true } shouldBe ROUNDS * THREADS
                outcomes.failuresByType().shouldBeEmpty()
            }
        }
    }

    private fun addAtOnce(add: (adder: Int) -> Boolean): List<Result<Boolean>> = StartGate.run(
        threads = THREADS,
        timeout = TIMEOUT,
        action = add,
    )

    private companion object {
        private const val ROUNDS = 20
        private const val THREADS = 8
        private val TIMEOUT = 5.seconds
    }
}
