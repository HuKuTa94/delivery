package github.com.hukuta94.delivery.api.scheduler.courier

import github.com.hukuta94.delivery.core.application.usecase.courier.MoveCouriersUseCase
import org.springframework.scheduling.annotation.Scheduled

class CourierScheduler(
    private val moveCouriersUseCase: MoveCouriersUseCase,
) {

    @Scheduled(fixedDelay = 1000)
    fun moveCouriers() {
        moveCouriersUseCase.execute()
    }

}