package github.com.hukuta94.delivery.core.domain

import java.util.*

class FakeDomainEvent : DomainEvent()
class FakeIntegrationEvent(id: UUID) : IntegrationEvent(id)

fun fakeDomainEvent() = FakeDomainEvent()
fun fakeIntegrationEvent(id: UUID? = null) = FakeIntegrationEvent(id ?: UUID.randomUUID())
