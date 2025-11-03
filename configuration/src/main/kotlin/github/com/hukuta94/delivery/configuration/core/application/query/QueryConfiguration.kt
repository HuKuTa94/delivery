package github.com.hukuta94.delivery.configuration.core.application.query

import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.application.query.order.GetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.converter.LocationConverter
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.query.courier.GetBusyCouriersQueryImpl
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.query.order.GetNotCompletedOrdersQueryImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

@Configuration
open class QueryConfiguration {

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

    @Bean
    open fun getNotCompletedOrdersQuery(
        jdbcTemplate: NamedParameterJdbcTemplate,
        locationConverter: LocationConverter,
    ): GetNotCompletedOrdersQuery = GetNotCompletedOrdersQueryImpl(
        jdbcTemplate = jdbcTemplate,
        locationConverter = locationConverter,
    )
}