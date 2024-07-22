package github.com.hukuta94.delivery.core.application.usecase.order.impl

import github.com.hukuta94.delivery.core.application.query.order.GetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.core.application.query.order.GetNotCompletedOrderResponse
import github.com.hukuta94.delivery.core.port.OrderRepository

//TODO Query use case должен получать необходимые данные в обход полного восстановления агрегата.
class GetNotCompletedOrdersQueryImpl(
    private val orderRepository: OrderRepository
) : GetNotCompletedOrdersQuery {
    override fun execute(): List<GetNotCompletedOrderResponse> {
        return orderRepository.getAllNotCompleted().map { order ->
            GetNotCompletedOrderResponse(
                orderId = order.id,
                locationAbscissa = order.location.abscissa,
                locationOrdinate = order.location.ordinate,
            )
        }
    }
}