package github.com.hukuta94.delivery.core.application.usecase.order.impl

import github.com.hukuta94.delivery.core.application.usecase.order.AssignCourierToOrderUseCase
import github.com.hukuta94.delivery.core.domain.service.DispatchService
import github.com.hukuta94.delivery.core.port.CourierRepository
import github.com.hukuta94.delivery.core.port.OrderRepository
import org.slf4j.LoggerFactory

class AssignCourierToOrderUseCaseImpl(
    private val orderRepository: OrderRepository,
    private val courierRepository: CourierRepository,
    private val dispatchService: DispatchService,
) : AssignCourierToOrderUseCase {
    override fun execute() {
        val orders = orderRepository.getAllCreated()
        val couriers = courierRepository.getAllFree()

        if (orders.isEmpty() || couriers.isEmpty()) {
            return
        }

        orders.forEach { order ->
            dispatchService.assignOrderToMostSuitableCourier(order, couriers)
        }

        orderRepository.update(orders)
        courierRepository.update(couriers)

        LOG.info("Orders were assigned to free couriers")
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(AssignCourierToOrderUseCaseImpl::class.java)
    }
}