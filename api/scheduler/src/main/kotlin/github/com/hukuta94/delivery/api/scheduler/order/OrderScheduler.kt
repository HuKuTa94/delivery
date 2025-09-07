package github.com.hukuta94.delivery.api.scheduler.order

import github.com.hukuta94.delivery.core.application.usecase.order.AssignCourierToOrderUseCase
import org.springframework.scheduling.annotation.Scheduled

class OrderScheduler(
    private val assignCourierToOrderUseCase: AssignCourierToOrderUseCase,
) {

    @Scheduled(fixedDelay = 2000)
    fun assignCourierToOrders() {
        assignCourierToOrderUseCase.execute()
    }

}