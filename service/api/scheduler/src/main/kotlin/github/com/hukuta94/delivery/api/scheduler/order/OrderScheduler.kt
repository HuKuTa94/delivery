package github.com.hukuta94.delivery.api.scheduler.order

import github.com.hukuta94.delivery.core.application.usecase.order.AssignCourierToOrderUseCase
import org.springframework.scheduling.annotation.Scheduled

class OrderScheduler(
    private val assignCourierToOrderUseCase: AssignCourierToOrderUseCase,
) {

    @Scheduled(
        fixedDelay = 2000,
        scheduler = TASK_SCHEDULER,
    )
    fun assignCourierToOrders() {
        assignCourierToOrderUseCase()
    }

    companion object {
        /**
         * Name of the single-thread scheduler bean of this job, so other jobs don't delay it
         */
        const val TASK_SCHEDULER = "orderAssignmentTaskScheduler"
    }
}
