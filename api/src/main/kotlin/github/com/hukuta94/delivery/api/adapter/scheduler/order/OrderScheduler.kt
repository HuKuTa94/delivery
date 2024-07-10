package github.com.hukuta94.delivery.api.adapter.scheduler.order

import github.com.hukuta94.delivery.core.application.usecase.order.command.AssignCourierToOrderCommand
import org.springframework.scheduling.annotation.Scheduled

class OrderScheduler(
    private val assignCourierToOrderCommand: AssignCourierToOrderCommand,
) {

    @Scheduled(fixedDelay = 2000)
    fun assignCourierToOrders() {
        assignCourierToOrderCommand.execute()
    }

}