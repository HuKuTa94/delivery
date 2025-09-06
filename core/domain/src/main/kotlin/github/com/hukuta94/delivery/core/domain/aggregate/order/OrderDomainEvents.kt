package github.com.hukuta94.delivery.core.domain.aggregate.order

import github.com.hukuta94.delivery.core.domain.DomainEvent
import java.util.*

sealed class OrderDomainEvent: DomainEvent()

data class OrderAssignedDomainEvent(val orderId: UUID, val courierId: UUID): OrderDomainEvent()

data class OrderCompletedDomainEvent(val orderId: UUID): OrderDomainEvent()