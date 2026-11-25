@file:Suppress(
    "MatchingDeclarationName",
    "Filename",
    "Reason: Events may be more than one in real application"
)

package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.DomainEvent
import java.util.UUID

data class BasketConfirmedEvent(
    val basketId: UUID,
    val street: String,
) : DomainEvent()
