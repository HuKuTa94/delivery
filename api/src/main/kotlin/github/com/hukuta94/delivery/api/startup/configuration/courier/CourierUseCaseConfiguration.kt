package github.com.hukuta94.delivery.api.startup.configuration.courier

import github.com.hukuta94.delivery.core.application.usecase.courier.command.impl.MoveCouriersCommandImpl
import github.com.hukuta94.delivery.core.application.usecase.courier.query.impl.GetFreeCouriersQueryImpl
import github.com.hukuta94.delivery.core.port.CourierRepository
import github.com.hukuta94.delivery.core.port.OrderRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CourierUseCaseConfiguration {

    @Bean
    open fun moveCouriersCommand(
        orderRepository: OrderRepository,
        courierRepository: CourierRepository
    ) = MoveCouriersCommandImpl(
        orderRepository = orderRepository,
        courierRepository = courierRepository,
    )

    @Bean
    open fun getFreeCouriersQuery(
        courierRepository: CourierRepository
    ) = GetFreeCouriersQueryImpl(
        courierRepository = courierRepository
    )
}