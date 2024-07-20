package github.com.hukuta94.delivery.api.startup.configuration.scheduler

import github.com.hukuta94.delivery.api.adapter.scheduler.courier.CourierScheduler
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