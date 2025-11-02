package github.com.hukuta94.delivery.core.domain.aggregate.order

import github.com.hukuta94.delivery.core.domain.DomainEvent
import java.util.*

data class OrderAssignedDomainEvent(
    val orderId: UUID,
    val courierId: UUID,
): DomainEvent()

data class OrderCompletedDomainEvent(
    val orderId: UUID,
): DomainEvent()