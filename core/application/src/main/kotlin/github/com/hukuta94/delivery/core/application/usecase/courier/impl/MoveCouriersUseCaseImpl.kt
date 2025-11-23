package github.com.hukuta94.delivery.core.application.usecase.courier.impl

import github.com.hukuta94.delivery.core.application.usecase.courier.MoveCouriersUseCase
import github.com.hukuta94.delivery.core.domain.aggregate.order.Order
import github.com.hukuta94.delivery.core.domain.rule.CompleteOrderBusinessRule
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import org.slf4j.LoggerFactory

class MoveCouriersUseCaseImpl(
    private val orderRepository: OrderRepositoryPort,
    private val courierRepository: CourierRepositoryPort,
    private val completeOrderBusinessRule: CompleteOrderBusinessRule,
    private val unitOfWork: UnitOfWorkPort,
) : MoveCouriersUseCase {

    override fun execute() {
        // TODO эта функция может быть портом-интерфейсом
        fun findCouriersWithOrders(): Map<Courier, Order> {
            val couriers = courierRepository.getAllBusy()
                .associateBy { it.id }
                .takeIf { it.isNotEmpty() }
                ?: return emptyMap()

            return orderRepository.getAllAssigned()
                .associateBy { requireNotNull(couriers[it.courierId]) }
                .takeIf { it.isNotEmpty() }
                ?: return emptyMap()
        }

        fun moveCouriersToTheirOrders(couriersWithOrders: Map<Courier, Order>) {
            LOG.info("Move couriers to their orders")

            couriersWithOrders.forEach { (courier, order) ->
                courier.moveTo(order.location)
            }
        }

        fun tryToCompleteOrders(couriersWithOrders: Map<Courier, Order>): Collection<Order> {
            val completedOrders = mutableListOf<Order>()

            couriersWithOrders.forEach { (courier, order) ->
                completeOrderBusinessRule.execute(order, courier).onRight {
                    LOG.info("Order with id: ${order.id} was completed by courier with id: ${courier.id}")
                    completedOrders.add(order)
                }
            }

            return completedOrders
        }

        unitOfWork.executeInTransaction {
            val couriersWithOrder = findCouriersWithOrders()
                .takeIf { it.isNotEmpty() }
                ?: return@executeInTransaction

            moveCouriersToTheirOrders(couriersWithOrder)
            tryToCompleteOrders(couriersWithOrder)
                .takeIf { it.isNotEmpty() }
                ?.let { orderRepository.update(it) }

            courierRepository.update(
                couriersWithOrder.keys.toList()
            )
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(MoveCouriersUseCaseImpl::class.java)
    }
}
