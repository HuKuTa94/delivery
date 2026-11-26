package github.com.hukuta94.delivery.api.scheduler.courier

import github.com.hukuta94.delivery.core.application.usecase.courier.MoveCouriersUseCase
import org.springframework.scheduling.annotation.Scheduled

class CourierScheduler(
    private val moveCouriersUseCase: MoveCouriersUseCase,
) {

    @Scheduled(
        fixedDelay = 1000,
        scheduler = TASK_SCHEDULER,
    )
    fun moveCouriers() {
        moveCouriersUseCase()
    }

    companion object {
        /**
         * Name of the single-thread scheduler bean of this job, so other jobs don't delay it
         */
        const val TASK_SCHEDULER = "courierMovementTaskScheduler"
    }
}
