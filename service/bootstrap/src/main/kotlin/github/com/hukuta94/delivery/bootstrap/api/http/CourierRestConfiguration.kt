package github.com.hukuta94.delivery.bootstrap.api.http

import github.com.hukuta94.delivery.api.http.courier.CourierControllerV1
import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.application.usecase.courier.RegisterCourierUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CourierRestConfiguration {
    @Bean
    open fun courierControllerV1(
        getBusyCouriersQuery: GetBusyCouriersQuery,
        registerCourierUseCase: RegisterCourierUseCase,
    ) = CourierControllerV1(
        getBusyCouriersQuery = getBusyCouriersQuery,
        registerCourierUseCase = registerCourierUseCase,
    )
}
