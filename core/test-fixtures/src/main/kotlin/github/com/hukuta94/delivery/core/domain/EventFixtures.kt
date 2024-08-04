package github.com.hukuta94.delivery.core.domain

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventClassType
import java.util.*

class FakeDomainEvent : DomainEvent()
class FakeIntegrationEvent(id: UUID) : IntegrationEvent(id)

fun fakeDomainEvent() = FakeDomainEvent()
fun fakeIntegrationEvent(id: UUID? = null) = FakeIntegrationEvent(id ?: UUID.randomUUID())

fun fakeDomainEventClassType() = DomainEventClassType(FakeDomainEvent::class)
fun fakeIntegrationEventClassType() = DomainEventClassType(FakeDomainEvent::class)
