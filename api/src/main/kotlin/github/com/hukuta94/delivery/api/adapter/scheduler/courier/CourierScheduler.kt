package github.com.hukuta94.delivery.api.adapter.scheduler.courier

import github.com.hukuta94.delivery.core.application.usecase.courier.command.MoveCouriersCommand
import org.springframework.scheduling.annotation.Scheduled

class CourierScheduler(
    private val moveCouriersCommand: MoveCouriersCommand,
) {

    @Scheduled(fixedDelay = 1000)
    fun moveCouriers() {
        moveCouriersCommand.execute()
    }

}