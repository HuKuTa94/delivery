package github.com.hukuta94.delivery.bootstrap.core.application.usecase

import github.com.hukuta94.delivery.core.application.usecase.order.AssignCourierToOrderUseCase
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase
import github.com.hukuta94.delivery.core.domain.rule.DispatchOrderToCourierBusinessRule
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.application.port.GetLocationPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import github.com.hukuta94.delivery.core.application.port.repository.event.OutboxEventRepositoryPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrderUseCaseConfiguration {

    @Bean
    open fun createOrderUseCase(
        orderRepository: OrderRepositoryPort,
        getLocationPort: GetLocationPort,
    ) = CreateOrderUseCase(
        orderRepository = orderRepository,
        getLocationPort = getLocationPort,
    )

    @Bean
    open fun assignCourierToOrderUseCase(
        orderRepository: OrderRepositoryPort,
        courierRepository: CourierRepositoryPort,
        dispatchOrderToCourierBusinessRule: DispatchOrderToCourierBusinessRule,
        unitOfWork: UnitOfWorkPort,
        outboxEventRepositoryPort: OutboxEventRepositoryPort,
    ) = AssignCourierToOrderUseCase(
        orderRepository = orderRepository,
        courierRepository = courierRepository,
        dispatchOrderToCourierBusinessRule = dispatchOrderToCourierBusinessRule,
        unitOfWork = unitOfWork,
        outboxEventRepositoryPort = outboxEventRepositoryPort,
    )
}
