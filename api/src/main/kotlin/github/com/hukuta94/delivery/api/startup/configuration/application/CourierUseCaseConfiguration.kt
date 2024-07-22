package github.com.hukuta94.delivery.api.startup.configuration.application

import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.application.usecase.courier.MoveCouriersUseCase
import github.com.hukuta94.delivery.core.application.usecase.courier.impl.MoveCouriersUseCaseImpl
import github.com.hukuta94.delivery.core.domain.service.CompleteOrderService
import github.com.hukuta94.delivery.core.port.CourierRepository
import github.com.hukuta94.delivery.core.port.OrderRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.LocationConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.query.courier.GetBusyCouriersQueryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
open class CourierUseCaseConfiguration {

    @Bean
    open fun MoveCouriersUseCase(
        orderRepository: OrderRepository,
        courierRepository: CourierRepository,
        completeOrderService: CompleteOrderService,
    ): MoveCouriersUseCase = MoveCouriersUseCaseImpl(
        orderRepository = orderRepository,
        courierRepository = courierRepository,
        completeOrderService = completeOrderService,
    )

    @Bean
    open fun locationConverter() = LocationConverter()

    @Bean
    open fun getBusyCouriersQuery(
        jdbcTemplate: NamedParameterJdbcTemplate,
        locationConverter: LocationConverter,
    ): GetBusyCouriersQuery = GetBusyCouriersQueryImpl(
        jdbcTemplate = jdbcTemplate,
        locationConverter = locationConverter,
    )
}