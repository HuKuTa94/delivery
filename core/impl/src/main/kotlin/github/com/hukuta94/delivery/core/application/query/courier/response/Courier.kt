package github.com.hukuta94.delivery.core.application.query.courier.response

import github.com.hukuta94.delivery.core.domain.courier.Transport
import java.util.*

data class Courier(
    val id: UUID,
    val name: String,
    val location: Location,
    val transport: Transport,
)