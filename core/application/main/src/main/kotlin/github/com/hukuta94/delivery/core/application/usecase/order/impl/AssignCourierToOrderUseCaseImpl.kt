package github.com.hukuta94.delivery.core.application.usecase.order.impl

import github.com.hukuta94.delivery.core.application.usecase.order.AssignCourierToOrderUseCase
import github.com.hukuta94.delivery.core.domain.service.DispatchService
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import org.slf4j.LoggerFactory

class AssignCourierToOrderUseCaseImpl(
    private val orderRepository: OrderRepositoryPort,
    private val courierRepository: CourierRepositoryPort,
    private val dispatchService: DispatchService,
    private val unitOfWork: UnitOfWorkPort,
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