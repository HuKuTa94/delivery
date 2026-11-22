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
    }
}
