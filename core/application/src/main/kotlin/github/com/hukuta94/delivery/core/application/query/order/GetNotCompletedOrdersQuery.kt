package github.com.hukuta94.delivery.core.application.query.order

import github.com.hukuta94.delivery.core.application.query.Query
import github.com.hukuta94.delivery.core.application.query.order.response.GetNotCompletedOrdersResponse

interface GetNotCompletedOrdersQuery : Query {
    fun execute(): GetNotCompletedOrdersResponse
}
