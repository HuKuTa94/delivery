package github.com.hukuta94.delivery.core.application.event.handler

import github.com.hukuta94.delivery.core.application.event.ApplicationEventHandler
import github.com.hukuta94.delivery.core.application.event.BasketConfirmedIntegrationDomainEvent
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderCommand
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase

class BasketConfirmedIntegrationDomainEventHandler(
    private val createOrderUseCase: CreateOrderUseCase,
) : ApplicationEventHandler<BasketConfirmedIntegrationDomainEvent> {

    override val eventType = BasketConfirmedIntegrationDomainEvent::class

    override fun handle(event: BasketConfirmedIntegrationDomainEvent) {
        val command = CreateOrderCommand(
            basketId = event.basketId,
            street = event.street,
        )
        createOrderUseCase.execute(command)
    }
}