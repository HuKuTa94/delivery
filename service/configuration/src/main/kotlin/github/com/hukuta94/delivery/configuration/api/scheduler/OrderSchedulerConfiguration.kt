package github.com.hukuta94.delivery.configuration.api.scheduler

import github.com.hukuta94.delivery.api.scheduler.order.OrderScheduler
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
