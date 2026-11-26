package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Transport
import github.com.hukuta94.delivery.core.domain.aggregate.courier.busyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.longestCourierName
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.integration.KtormIntegrationSpec
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.enum

internal class KtormCourierRepositoryIT : KtormIntegrationSpec() {
    init {
        val sut = KtormCourierRepository(database)

        "add then getById round-trips a courier" {
            val repository = KtormCourierRepository(database)
            val courier = freeCourier(
                location = location(7, 8),
                transport = Transport.BICYCLE
            )

            repository.add(courier)

            val loaded = repository.getById(courier.id)
            assertSoftly {
                loaded.id shouldBe courier.id
                loaded.name.value shouldBe courier.name.value
                loaded.status shouldBe CourierStatus.FREE
                loaded.location shouldBe courier.location
                loaded.transport shouldBe Transport.BICYCLE
            }
        }

        "getAllFree and getAllBusy filter by status" {
            val repository = KtormCourierRepository(database)
            val free = freeCourier()
            val busy = busyCourier()
            repository.add(free)
            repository.add(busy)

            repository.getAllFree().map { it.id } shouldContainExactlyInAnyOrder listOf(free.id)
            repository.getAllBusy().map { it.id } shouldContainExactlyInAnyOrder listOf(busy.id)
        }

        "batch update persists the new status of every courier" {
            val repository = KtormCourierRepository(database)
            val first = freeCourier()
            val second = freeCourier()
            repository.add(first)
            repository.add(second)

            first.busy()
            second.busy()
            repository.update(listOf(first, second))

            repository.getAllBusy().map { it.id } shouldContainExactlyInAnyOrder listOf(first.id, second.id)
            repository.getAllFree().shouldBeEmpty()
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

        "a courier on any kind of transport is stored with it" {
            checkAll(Exhaustive.enum<Transport>()) { transport ->
                // Given
                val courier = freeCourier(transport = transport)

                // When
                sut.add(courier)

                // Then
                sut.getById(courier.id).transport shouldBe transport
            }
        }

        "the longest allowed courier name is stored in full" {
            // Given
            val courier = freeCourier(name = longestCourierName())

            // When
            sut.add(courier)

            // Then
            sut.getById(courier.id).name shouldBe courier.name
        }
    }
}
