package github.com.hukuta94.delivery.core.application.query.courier

import github.com.hukuta94.delivery.core.application.query.Query
import github.com.hukuta94.delivery.core.application.query.courier.response.GetCouriersResponse

interface GetBusyCouriersQuery : Query {
    fun execute(): GetCouriersResponse
}
