package github.com.hukuta94.delivery.core.application.usecase.order

import arrow.core.getOrElse
import github.com.hukuta94.delivery.core.application.port.GetLocationPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryPort
import github.com.hukuta94.delivery.core.application.usecase.UseCase
import github.com.hukuta94.delivery.core.domain.aggregate.order.Order
import org.slf4j.LoggerFactory
import java.util.*

class CreateOrderUseCase(
    private val orderRepository: OrderRepositoryPort,
    private val getLocationPort: GetLocationPort,
) : UseCase {
    operator fun invoke(command: CreateOrderCommand) {
        val orderId = command.basketId

        val orderLocation = getLocationPort.fromStreet(command.street).getOrElse { error(it.message) }
        val newOrder = Order.create(
            id = orderId,
            location = orderLocation,
        )

        val added = orderRepository.add(newOrder)
        if (added) {
            LOG.info("Order with basket id: $orderId was created")
        } else {
            LOG.info("Order with basket id: $orderId already exists, skipped")
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(CreateOrderUseCase::class.java)
    }
}

data class CreateOrderCommand(
    val basketId: UUID,
    val street: String,
)
