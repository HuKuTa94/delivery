package github.com.hukuta94.delivery.core.application.usecase.order.command.impl

import github.com.hukuta94.delivery.core.application.usecase.courier.command.MoveCouriersCommand
import github.com.hukuta94.delivery.core.application.usecase.order.command.AssignCourierToOrderCommand
import github.com.hukuta94.delivery.core.domain.service.DispatchService
import github.com.hukuta94.delivery.core.port.CourierRepository
import github.com.hukuta94.delivery.core.port.OrderRepository
import org.slf4j.LoggerFactory

class AssignCourierToOrderCommandImpl(
    private val orderRepository: OrderRepository,
    private val courierRepository: CourierRepository,
    private val dispatchService: DispatchService,
) : AssignCourierToOrderCommand {
    override fun execute() {
        LOG.info("Try to assign orders to free couriers")

        val orders = orderRepository.getAllCreated()
        val couriers = courierRepository.getAllFree()

        orders.forEach { order ->
            dispatchService.assignOrderToMostSuitableCourier(order, couriers)
        }

        orderRepository.update(orders)
        courierRepository.update(couriers)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(MoveCouriersCommand::class.java)
    }
}