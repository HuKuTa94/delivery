package github.com.hukuta94.delivery.configuration.core.application.usecase

import github.com.hukuta94.delivery.core.application.usecase.courier.MoveCouriersUseCase
import github.com.hukuta94.delivery.core.application.usecase.courier.impl.MoveCouriersUseCaseImpl
import github.com.hukuta94.delivery.core.domain.rule.CompleteOrderBusinessRule
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CourierUseCaseConfiguration {

    @Bean
    open fun MoveCouriersUseCase(
        orderRepository: OrderRepositoryPort,
        courierRepository: CourierRepositoryPort,
        completeOrderBusinessRule: CompleteOrderBusinessRule,
        unitOfWork: UnitOfWorkPort,
    ): MoveCouriersUseCase = MoveCouriersUseCaseImpl(
        orderRepository = orderRepository,
        courierRepository = courierRepository,
        completeOrderBusinessRule = completeOrderBusinessRule,
        unitOfWork = unitOfWork,
    )
}