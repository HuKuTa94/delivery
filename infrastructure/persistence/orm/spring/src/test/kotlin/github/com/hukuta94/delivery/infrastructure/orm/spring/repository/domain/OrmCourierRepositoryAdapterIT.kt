package github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Transport
import github.com.hukuta94.delivery.core.domain.aggregate.courier.busyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.infrastructure.orm.spring.integration.SpringJpaIntegrationSpec
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

internal class OrmCourierRepositoryAdapterIT : SpringJpaIntegrationSpec() {
    init {
        val courierAdapter = OrmCourierRepositoryAdapter(context.getBean(CourierJpaRepository::class.java))
        val sut = courierAdapter

        "add then getById round-trips a courier" {
            val courier = freeCourier(
                location = location(7, 8),
                transport = Transport.BICYCLE,
            )

            courierAdapter.add(courier)

            val loaded = courierAdapter.getById(courier.id)
            assertSoftly {
                loaded.id shouldBe courier.id
                loaded.name.value shouldBe courier.name.value
                loaded.location shouldBe courier.location
                loaded.transport shouldBe Transport.BICYCLE
                loaded.status shouldBe CourierStatus.FREE
            }
        }

        "getAllFree and getAllBusy filter by status" {
            val free = freeCourier()
            val busy = busyCourier()
            courierAdapter.add(free)
            courierAdapter.add(busy)

            courierAdapter.getAllFree().map { it.id } shouldContainExactlyInAnyOrder listOf(free.id)
            courierAdapter.getAllBusy().map { it.id } shouldContainExactlyInAnyOrder listOf(busy.id)
        }

        "a new courier is reported as added" {
            // When
            val added = sut.add(freeCourier())

            // Then
            added shouldBe true
        }

        "a courier added again is reported as not added and keeps its stored state" {
            // Given
            val stored = busyCourier(
                location = location(4, 5),
                transport = Transport.CAR,
            )
            sut.add(stored)
            val again = freeCourier(
                id = stored.id,
                location = location(9, 9),
                transport = Transport.BICYCLE,
            )

            // When
            val readd = runCatching { sut.add(again) }

            // Then
            val loaded = sut.getById(stored.id)
            assertSoftly {
                readd shouldBe Result.success(false)
                loaded.name.value shouldBe stored.name.value
                loaded.status shouldBe CourierStatus.BUSY
                loaded.location shouldBe location(4, 5)
                loaded.transport shouldBe Transport.CAR
            }
        }
    }
}
