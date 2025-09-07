package github.com.hukuta94.delivery.configuration.api.scheduler

import github.com.hukuta94.delivery.api.scheduler.courier.CourierScheduler
import github.com.hukuta94.delivery.core.application.usecase.courier.MoveCouriersUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CourierSchedulerConfiguration {

    @Bean
    open fun courierScheduler(
        moveCouriersUseCase: MoveCouriersUseCase,
    ) = CourierScheduler(
        moveCouriersUseCase = moveCouriersUseCase,
    )
}