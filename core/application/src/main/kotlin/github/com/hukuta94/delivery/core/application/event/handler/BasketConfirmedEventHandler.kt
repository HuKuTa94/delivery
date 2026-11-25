package github.com.hukuta94.delivery.core.application.event.handler

import github.com.hukuta94.delivery.core.application.event.EventHandler
import github.com.hukuta94.delivery.core.application.event.BasketConfirmedEvent
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderCommand
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase

class BasketConfirmedEventHandler(
    private val createOrderUseCase: CreateOrderUseCase,
) : EventHandler<BasketConfirmedEvent> {

    override val eventType = BasketConfirmedEvent::class

    override fun handle(event: BasketConfirmedEvent) {
        val command = CreateOrderCommand(
            basketId = event.basketId,
            street = event.street,
        )
        createOrderUseCase(command)
    }
}
