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

        LOG.info("Try to create new order with basket id: $orderId")

        val orderAlreadyExists = orderRepository.existsById(command.basketId)
        if (orderAlreadyExists) {
            LOG.info("Order with id: $orderId already exists")
            return
        }

        val orderLocation = getLocationPort.fromStreet(command.street).getOrElse { error(it.message) }
        val newOrder = Order.create(
            id = orderId,
            location = orderLocation,
        )

        orderRepository.add(newOrder)

        LOG.info("Order with basket id: $orderId was created")
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(CreateOrderUseCase::class.java)
    }
}

data class CreateOrderCommand(
    val basketId: UUID,
    val street: String,
)
