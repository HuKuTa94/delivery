package github.com.hukuta94.delivery.api.startup.configuration.courier

import github.com.hukuta94.delivery.api.adapter.http.courier.CourierControllerV1
import github.com.hukuta94.delivery.core.application.usecase.courier.query.GetBusyCouriersQuery
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CourierControllerConfiguration {
    @Bean
    open fun courierControllerV1(
        getBusyCouriersQuery: GetBusyCouriersQuery,
    ) = CourierControllerV1(
        getBusyCouriersQuery = getBusyCouriersQuery,
    )
}