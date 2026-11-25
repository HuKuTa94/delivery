package github.com.hukuta94.delivery.configuration.api.http

import github.com.hukuta94.delivery.api.http.courier.CourierControllerV1
import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CourierRestConfiguration {
    @Bean
    open fun courierControllerV1(
        getBusyCouriersQuery: GetBusyCouriersQuery,
    ) = CourierControllerV1(
        getBusyCouriersQuery = getBusyCouriersQuery,
    )
}
