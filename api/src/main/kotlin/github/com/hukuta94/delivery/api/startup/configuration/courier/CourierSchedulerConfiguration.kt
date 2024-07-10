package github.com.hukuta94.delivery.api.startup.configuration.courier

import github.com.hukuta94.delivery.api.adapter.scheduler.courier.CourierScheduler
import github.com.hukuta94.delivery.core.application.usecase.courier.command.MoveCouriersCommand
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CourierSchedulerConfiguration {

    @Bean
    open fun courierScheduler(
        moveCouriersCommand: MoveCouriersCommand,
    ) = CourierScheduler(
        moveCouriersCommand = moveCouriersCommand,
    )
}