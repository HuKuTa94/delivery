package github.com.hukuta94.delivery.core.application.usecase.order.command.impl

import github.com.hukuta94.delivery.core.application.usecase.order.command.AssignCourierToOrderCommand
import github.com.hukuta94.delivery.core.domain.service.DispatchService
import github.com.hukuta94.delivery.core.port.CourierRepository
import github.com.hukuta94.delivery.core.port.OrderRepository

class AssignCourierToOrderCommandImpl(
    private val orderRepository: OrderRepository,
    private val courierRepository: CourierRepository,
    private val dispatchService: DispatchService,
) : AssignCourierToOrderCommand {
    override fun execute() {
        val orders = orderRepository.getAllCreated()
        val couriers = courierRepository.getAllFree()

        orders.forEach { order ->
            dispatchService.assignOrderToMostSuitableCourier(order, couriers)
        }

        orderRepository.update(orders)
        courierRepository.update(couriers)
    }
}