package github.com.hukuta94.delivery.core.application.query.courier

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