package github.com.hukuta94.delivery.core.application.usecase.order.command.impl

import github.com.hukuta94.delivery.core.application.usecase.courier.command.MoveCouriersCommand
import github.com.hukuta94.delivery.core.application.usecase.order.command.Command
import github.com.hukuta94.delivery.core.application.usecase.order.command.CreateOrderCommand
import github.com.hukuta94.delivery.core.domain.order.Order
import github.com.hukuta94.delivery.core.port.GetLocationPort
import github.com.hukuta94.delivery.core.port.OrderRepository
import org.slf4j.LoggerFactory

class CreateOrderCommandImpl(
    private val orderRepository: OrderRepository,
    private val getLocationPort: GetLocationPort,
) : CreateOrderCommand {
    override fun execute(command: Command) {
        LOG.info("Try to create new order with basket id: ${command.basketId}")

        val newOrder = Order.create(
            id = command.basketId,
            location = getLocationPort.getFromStreet(command.street),
        )

        orderRepository.add(newOrder)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(MoveCouriersCommand::class.java)
    }
}