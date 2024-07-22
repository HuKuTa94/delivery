package github.com.hukuta94.delivery.core.application.query.courier

import java.util.*

interface GetFreeCouriersQuery {
    fun execute(): List<GetFreeCourierResponse>
}

data class GetFreeCourierResponse(
    val id: UUID,
    val name: String,
    val locationAbscissa: Int,
    val locationOrdinate: Int,
)