package github.com.hukuta94.delivery.core.application.usecase.order.command.impl

import github.com.hukuta94.delivery.core.application.usecase.order.command.Command
import github.com.hukuta94.delivery.core.application.usecase.order.command.CreateOrderCommand
import github.com.hukuta94.delivery.core.domain.order.Order
import github.com.hukuta94.delivery.core.domain.sharedkernel.Location
import github.com.hukuta94.delivery.core.port.OrderRepository

class CreateOrderCommandImpl(
    private val orderRepository: OrderRepository
) : CreateOrderCommand {
    override fun execute(command: Command) {
        orderRepository.add(
            Order.create(
                id = command.basketId,
                location = Location.random(),
            )
        )
    }
}