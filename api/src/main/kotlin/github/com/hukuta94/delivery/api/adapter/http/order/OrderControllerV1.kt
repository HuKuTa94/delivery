package github.com.hukuta94.delivery.api.adapter.http.order

import github.com.hukuta94.delivery.api.adapter.http.common.Location
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderCommand
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase
import github.com.hukuta94.delivery.core.application.usecase.order.GetNotCompletedOrdersQuery
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1")
class OrderControllerV1(
    private val createOrderUseCase: CreateOrderUseCase,
    private val getNotCompletedOrdersQuery: GetNotCompletedOrdersQuery,
) {

    @PostMapping("orders")
    fun createOrder(): ResponseEntity<Void> {
        val command = CreateOrderCommand(
            basketId = UUID.randomUUID(),
            street = "Random street",
        )
        createOrderUseCase.execute(command)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping("orders/active")
    fun getActiveOrders(): ResponseEntity<List<Order>> {
        val activeOrders = getNotCompletedOrdersQuery.execute().map {
            Order(
                id = it.orderId,
                courierLocation = Location(it.locationAbscissa, it.locationOrdinate)
            )
        }
        return ResponseEntity.ok(activeOrders)
    }
}

