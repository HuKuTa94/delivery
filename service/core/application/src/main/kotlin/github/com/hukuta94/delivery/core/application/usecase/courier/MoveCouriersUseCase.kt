package github.com.hukuta94.delivery.core.application.usecase.courier

import github.com.hukuta94.delivery.core.application.usecase.UseCase
import github.com.hukuta94.delivery.core.domain.aggregate.order.Order
import github.com.hukuta94.delivery.core.domain.rule.CompleteOrderBusinessRule
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import github.com.hukuta94.delivery.core.application.port.repository.event.OutboxEventRepositoryPort
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import org.slf4j.LoggerFactory

class MoveCouriersUseCase(
    private val orderRepository: OrderRepositoryPort,
    private val courierRepository: CourierRepositoryPort,
    private val completeOrderBusinessRule: CompleteOrderBusinessRule,
    private val unitOfWork: UnitOfWorkPort,
    private val outboxEventRepositoryPort: OutboxEventRepositoryPort,
) : UseCase {

    operator fun invoke() {
        // TODO эта функция может быть портом-интерфейсом
        fun findCouriersWithOrders(): Map<Courier, Order> {
            val couriers = courierRepository.getAllBusy()
                .associateBy { it.id }
                .takeIf { it.isNotEmpty() }
                ?: return emptyMap()

            // The courier of an assigned order may be missing from the busy ones, e.g. when it was dispatched
            // between the two reads. Such an order is skipped, so it doesn't stop the other couriers.
            return orderRepository.getAllAssigned()
                .mapNotNull { order ->
                    val courier = couriers[order.courierId]
                    if (courier == null) {
                        LOG.warn(
                            "Skip order with id: ${order.id}: its courier with id: ${order.courierId} is not busy",
                        )
                    }
                    courier?.let { it to order }
                }
                .toMap()
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
            val completedOrders = tryToCompleteOrders(couriersWithOrder)
            val couriers = couriersWithOrder.keys.toList()

            // Single explicit place to collect domain events from all mutated aggregates
            val domainEvents = (completedOrders + couriers).flatMap { it.popDomainEvents() }
            outboxEventRepositoryPort.saveDomainEvents(domainEvents)

            completedOrders
                .takeIf { it.isNotEmpty() }
                ?.let { orderRepository.update(it) }

            courierRepository.update(couriers)
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(MoveCouriersUseCase::class.java)
    }
}
