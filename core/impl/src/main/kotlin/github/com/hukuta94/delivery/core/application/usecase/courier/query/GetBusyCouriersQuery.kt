package github.com.hukuta94.delivery.core.application.usecase.courier.query

import java.util.*

interface GetBusyCouriersQuery {
    fun execute(): List<GetBusyCourierResponse>
}

data class GetBusyCourierResponse(
    val id: UUID,
    val name: String,
    val locationAbscissa: Int,
    val locationOrdinate: Int,
)