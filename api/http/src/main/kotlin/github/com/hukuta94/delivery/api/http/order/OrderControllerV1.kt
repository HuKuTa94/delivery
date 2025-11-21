package github.com.hukuta94.delivery.api.http.order

import github.com.hukuta94.delivery.core.application.event.BasketConfirmedIntegrationDomainEvent
import github.com.hukuta94.delivery.core.application.query.order.GetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.core.application.query.order.response.OrderResponse
import github.com.hukuta94.delivery.core.application.usecase.event.SaveIntegrationEventUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1")
class OrderControllerV1(
    private val getNotCompletedOrdersQuery: GetNotCompletedOrdersQuery,
    private val saveIntegrationEventUseCase: SaveIntegrationEventUseCase,
) {

    @PostMapping("orders/")
    fun createOrder(): ResponseEntity<Void> {
        val event = BasketConfirmedIntegrationDomainEvent(
            basketId = UUID.randomUUID(),
            street = "Random street",
        )
        saveIntegrationEventUseCase.execute(event)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping("orders/active/")
    fun getActiveOrders(): ResponseEntity<List<OrderResponse>> {
        val response = getNotCompletedOrdersQuery.execute()
        return ResponseEntity.ok(response.orders)
    }
}
