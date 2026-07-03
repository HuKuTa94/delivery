package github.com.hukuta94.delivery.infrastructure.orm.spring.query.courier

import github.com.hukuta94.delivery.core.application.query.common.LocationResponse
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Transport
import github.com.hukuta94.delivery.core.domain.aggregate.courier.busyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.infrastructure.orm.spring.integration.SpringJpaIntegrationSpec
import github.com.hukuta94.delivery.infrastructure.orm.spring.model.converter.LocationConverter
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain.CourierJpaRepository
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain.OrmCourierRepositoryAdapter
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

internal class GetBusyCouriersQueryImplIT : SpringJpaIntegrationSpec() {
    init {
        val courierAdapter = OrmCourierRepositoryAdapter(context.getBean(CourierJpaRepository::class.java))
        val sut = GetBusyCouriersQueryImpl(
            context.getBean(NamedParameterJdbcTemplate::class.java),
            LocationConverter(),
        )

        "returns only BUSY couriers (excludes FREE) and maps fields" {
            courierAdapter.add(freeCourier())
            val busy = busyCourier(
                location = location(4, 5),
                transport = Transport.CAR,
            )
            courierAdapter.add(busy)

            val response = sut.execute()

            val mapped = response.couriers.single()
            assertSoftly {
                mapped.id shouldBe busy.id
                mapped.name shouldBe busy.name.value
                mapped.location shouldBe LocationResponse(4, 5)
                mapped.transportId shouldBe Transport.CAR.id
            }
        }
    }
}
