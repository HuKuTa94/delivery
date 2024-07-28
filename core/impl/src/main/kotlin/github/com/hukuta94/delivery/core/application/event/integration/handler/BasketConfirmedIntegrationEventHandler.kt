package github.com.hukuta94.delivery.core.application.event.integration.handler

import github.com.hukuta94.delivery.core.application.event.integration.BasketConfirmedIntegrationEvent
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderCommand
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase

class BasketConfirmedIntegrationEventHandler(
    private val createOrderUseCase: CreateOrderUseCase,
) : IntegrationEventHandler<BasketConfirmedIntegrationEvent> {

    override val eventType = BasketConfirmedIntegrationEvent::class

    override fun handle(event: BasketConfirmedIntegrationEvent) {
        val command = CreateOrderCommand(
            basketId = event.basketId,
            street = event.street,
        )
        createOrderUseCase.execute(command)
    }
}