package github.com.hukuta94.delivery.core.application.usecase.order.impl

import github.com.hukuta94.delivery.core.application.usecase.order.AssignCourierToOrderUseCase
import github.com.hukuta94.delivery.core.domain.service.DispatchService
import github.com.hukuta94.delivery.core.port.CourierRepository
import github.com.hukuta94.delivery.core.port.OrderRepository
import github.com.hukuta94.delivery.core.port.UnitOfWork
import org.slf4j.LoggerFactory

class AssignCourierToOrderUseCaseImpl(
    private val orderRepository: OrderRepository,
    private val courierRepository: CourierRepository,
    private val dispatchService: DispatchService,
    private val unitOfWork: UnitOfWork,
) : AssignCourierToOrderUseCase {
    override fun execute() {
        unitOfWork.executeInTransaction {
            val orders = orderRepository.getAllCreated()
            val couriers = courierRepository.getAllFree()

            if (orders.isEmpty() || couriers.isEmpty()) {
                return@executeInTransaction
            }


            orders.forEach { order ->
                dispatchService.assignOrderToMostSuitableCourier(order, couriers)
            }

            orderRepository.update(orders)
            courierRepository.update(couriers)

            LOG.info("Orders were assigned to free couriers")
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(AssignCourierToOrderUseCaseImpl::class.java)
    }
}