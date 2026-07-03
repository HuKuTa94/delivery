package github.com.hukuta94.delivery.infrastructure.orm.ktorm.query

import github.com.hukuta94.delivery.core.application.query.common.LocationResponse
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Transport
import github.com.hukuta94.delivery.core.domain.aggregate.courier.busyCourier
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.integration.KtormIntegrationSpec
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.KtormCourierRepository
import io.kotest.matchers.shouldBe

internal class KtormGetBusyCouriersQueryIT : KtormIntegrationSpec() {
    init {
        // NOTE: KtormGetBusyCouriersQuery currently does NOT filter by status (returns all couriers) —
        // a divergence from the JDBC implementation, out of this epic's scope. This test verifies the
        // column mapping by inserting only a busy courier.
        "maps a busy courier's fields correctly" {
            val courierRepository = KtormCourierRepository(database)
            val busy = busyCourier(location = location(4, 5), transport = Transport.CAR)
            courierRepository.add(busy)

            val response = KtormGetBusyCouriersQuery(database).execute()

            val mapped = response.couriers.single { it.id == busy.id }
            mapped.name shouldBe busy.name.value
            mapped.location shouldBe LocationResponse(4, 5)
            mapped.transportId shouldBe Transport.CAR.id
        }
    }
}
