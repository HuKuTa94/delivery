@file:Suppress(
    "MatchingDeclarationName",
    "Filename",
    "Reason: Integration events may be more than one in real application"
)

package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.DomainEvent
import java.util.*

data class BasketConfirmedIntegrationDomainEvent(
    val basketId: UUID,
    val street: String,
) : DomainEvent()
