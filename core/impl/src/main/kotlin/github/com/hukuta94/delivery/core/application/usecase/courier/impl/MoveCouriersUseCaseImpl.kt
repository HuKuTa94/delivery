package github.com.hukuta94.delivery.core.application.usecase.courier.impl

import github.com.hukuta94.delivery.core.application.usecase.courier.MoveCouriersUseCase
import github.com.hukuta94.delivery.core.domain.order.Order
import github.com.hukuta94.delivery.core.domain.service.CompleteOrderService
import github.com.hukuta94.delivery.core.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.port.repository.domain.OrderRepositoryPort
import github.com.hukuta94.delivery.core.port.repository.UnitOfWorkPort
import org.slf4j.LoggerFactory

class MoveCouriersUseCaseImpl(
    private val orderRepository: OrderRepositoryPort,
    private val courierRepository: CourierRepositoryPort,
    private val completeOrderService: CompleteOrderService,
    private val unitOfWork: UnitOfWorkPort,
) : MoveCouriersUseCase {

    override fun execute() {
        unitOfWork.executeInTransaction {
            val busyCouriers = courierRepository.getAllBusy().associateBy { it.id }
            if (busyCouriers.keys.isEmpty()) {
                return@executeInTransaction
            }

            val courierByOrderMap = orderRepository.getAllAssigned().associateBy {
                requireNotNull(
                    busyCouriers[it.courierId]
                )
            }
            if (courierByOrderMap.isEmpty()) {
                return@executeInTransaction
            }

            LOG.info("Move couriers to their orders")

            val ordersToUpdate = mutableListOf<Order>()

            courierByOrderMap.forEach { (courier, order) ->
                // Move couriers to their orders
                courier.moveTo(order.location)

                // Try to complete the order
                val isOrderCompleted = completeOrderService.execute(order, courier)
                if (isOrderCompleted) {
                    LOG.info("Order with id: ${order.id} was completed by courier with id: ${courier.id}")
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

    companion object {
        private val LOG = LoggerFactory.getLogger(MoveCouriersUseCaseImpl::class.java)
    }
}