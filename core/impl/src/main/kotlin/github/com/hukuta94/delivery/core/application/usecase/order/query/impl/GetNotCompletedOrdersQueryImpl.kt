package github.com.hukuta94.delivery.core.application.usecase.order.query.impl

import github.com.hukuta94.delivery.core.application.usecase.order.query.GetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.core.application.usecase.order.query.Response
import github.com.hukuta94.delivery.core.port.OrderRepository

//TODO Query use case должен получать необходимые данные в обход полного восстановления агрегата.
class GetNotCompletedOrdersQueryImpl(
    private val orderRepository: OrderRepository
) : GetNotCompletedOrdersQuery {
    override fun execute(): List<Response> {
        return orderRepository.getAllNotCompleted().map { order ->
            Response(
                orderId = order.id,
                locationAbscissa = order.location.abscissa,
                locationOrdinate = order.location.ordinate,
            )
        }
    }
}