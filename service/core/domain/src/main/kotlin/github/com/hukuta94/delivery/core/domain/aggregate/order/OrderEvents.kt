package github.com.hukuta94.delivery.core.domain.aggregate.order

import github.com.hukuta94.delivery.core.domain.DomainEvent
import java.util.*

data class OrderAssignedEvent(
    val orderId: UUID,
    val courierId: UUID,
) : DomainEvent()

data class OrderCompletedEvent(
    val orderId: UUID,
) : DomainEvent()
