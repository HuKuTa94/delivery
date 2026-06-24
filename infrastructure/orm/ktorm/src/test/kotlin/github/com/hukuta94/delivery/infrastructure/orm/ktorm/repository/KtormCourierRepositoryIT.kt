package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository

import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Transport
import github.com.hukuta94.delivery.core.domain.aggregate.courier.busyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.integration.KtormIntegrationSpec
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe

internal class KtormCourierRepositoryIT : KtormIntegrationSpec() {
    init {
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
    }
}
