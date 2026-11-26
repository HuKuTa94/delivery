package github.com.hukuta94.delivery.core.application.usecase.order

import github.com.hukuta94.delivery.core.application.usecase.UseCase
import github.com.hukuta94.delivery.core.domain.rule.DispatchOrderToCourierBusinessRule
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import github.com.hukuta94.delivery.core.application.port.repository.event.OutboxEventRepositoryPort
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.order.Order
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import org.slf4j.LoggerFactory

class AssignCourierToOrderUseCase(
    private val orderRepository: OrderRepositoryPort,
    private val courierRepository: CourierRepositoryPort,
    private val dispatchOrderToCourierBusinessRule: DispatchOrderToCourierBusinessRule,
    private val unitOfWork: UnitOfWorkPort,
    private val outboxEventRepositoryPort: OutboxEventRepositoryPort,
) : UseCase {
    operator fun invoke() {
        unitOfWork.executeInTransaction {
            val orders = orderRepository.getAllCreated()
            val couriers = courierRepository.getAllFree()

            val assignedOrders = dispatch(
                orders = orders,
                couriers = couriers,
            )
            // Nothing changed, nothing to save
            if (assignedOrders.isEmpty()) {
                return@executeInTransaction
            }
            // Only the assigned orders and their couriers changed. Saving the others would write them back as they
            // were read, over a change another job may have made since
            val assignedCourierIds = assignedOrders.map { it.courierId }.toSet()
            val assignedCouriers = couriers.filter { it.id in assignedCourierIds }
            val domainEvents = (orders + couriers).flatMap { it.popDomainEvents() }

            outboxEventRepositoryPort.saveDomainEvents(domainEvents)
            orderRepository.update(assignedOrders)
            courierRepository.update(assignedCouriers)

            LOG.info("${assignedOrders.size} orders were assigned to free couriers")
        }
    }

    /**
     * Gives the created [orders] to the free [couriers] by the dispatch rule.
     *
     * @return the orders that got a courier
     */
    private fun dispatch(
        orders: Collection<Order>,
        couriers: Collection<Courier>,
    ): List<Order> {
        orders.forEach { order ->
            dispatchOrderToCourierBusinessRule.execute(order, couriers)
        }
        return orders.filter { it.status == OrderStatus.ASSIGNED }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(AssignCourierToOrderUseCase::class.java)
    }
}
