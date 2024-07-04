package github.com.hukuta94.delivery.core.application.usecase.courier.query

import java.util.*

interface GetFreeCouriersQuery {
    fun execute(): List<Response>
}

data class Response(
    val id: UUID,
    val name: String,
    val locationAbscissa: Int,
    val locationOrdinate: Int,
)