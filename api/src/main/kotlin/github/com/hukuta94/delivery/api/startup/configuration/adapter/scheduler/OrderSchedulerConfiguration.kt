package github.com.hukuta94.delivery.api.startup.configuration.adapter.scheduler

import github.com.hukuta94.delivery.api.adapter.scheduler.order.OrderScheduler
import github.com.hukuta94.delivery.core.application.usecase.order.AssignCourierToOrderUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class OrderSchedulerConfiguration {

    @Bean
    open fun orderScheduler(
        assignCourierToOrderUseCase: AssignCourierToOrderUseCase,
    ) = OrderScheduler(
        assignCourierToOrderUseCase = assignCourierToOrderUseCase,
    )
}