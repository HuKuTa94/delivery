@file:Suppress(
    "MatchingDeclarationName",
    "Filename",
    "Reason: In real application may be more events"
)

package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.FakeDomainEvent
import java.util.UUID

class FakeEventHandler : EventHandler<FakeDomainEvent> {
    var executedCount: Int = 0
        private set

    override val eventType = FakeDomainEvent::class

    override fun handle(event: FakeDomainEvent) {
        executedCount++
    }
}

fun basketConfirmedIntegrationDomainEvent(
    basketId: UUID = UUID.randomUUID(),
    street: String = "Default street",
) = BasketConfirmedEvent(
    basketId = basketId,
    street = street,
)
