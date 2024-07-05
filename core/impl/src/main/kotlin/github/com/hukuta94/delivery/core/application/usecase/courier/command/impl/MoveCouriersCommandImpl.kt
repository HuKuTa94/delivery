package github.com.hukuta94.delivery.core.application.usecase.courier.command.impl

import github.com.hukuta94.delivery.core.application.usecase.courier.command.MoveCouriersCommand
import github.com.hukuta94.delivery.core.domain.order.Order
import github.com.hukuta94.delivery.core.domain.service.CompleteOrderService
import github.com.hukuta94.delivery.core.port.CourierRepository
import github.com.hukuta94.delivery.core.port.OrderRepository

class MoveCouriersCommandImpl(
    private val orderRepository: OrderRepository,
    private val courierRepository: CourierRepository,
    private val completeOrderService: CompleteOrderService,
) : MoveCouriersCommand {
    override fun execute() {
        val busyCouriers = courierRepository.getAllBusy().associateBy { it.id }
        if (busyCouriers.keys.isEmpty()) {
            return
        }

        val assignedOrders = orderRepository.getAllAssigned()

        val courierByOrderMap = assignedOrders.associateBy {
            requireNotNull(busyCouriers[it.courierId])
        }

        val ordersToUpdate = mutableListOf<Order>()

        courierByOrderMap.forEach { (courier, order) ->
            // Move couriers to their orders
            courier.moveTo(order.location)

            // Try to complete the order
            val isOrderCompleted = completeOrderService.execute(order, courier)
            if (isOrderCompleted) {
                ordersToUpdate.add(order)
            }
        }

        // Always update couriers after movement
        courierRepository.update(
            courierByOrderMap.keys.toList()
        )

        // Update completed orders
        if (ordersToUpdate.isNotEmpty()) {
            orderRepository.update(ordersToUpdate)
        }
    }
}