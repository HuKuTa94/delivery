package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventClassType
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventClassType
import github.com.hukuta94.delivery.core.domain.FakeDomainEvent
import github.com.hukuta94.delivery.core.domain.FakeIntegrationEvent

fun fakeDomainEventClassType() = DomainEventClassType(FakeDomainEvent::class)

fun fakeIntegrationEventClassType() = IntegrationEventClassType(FakeIntegrationEvent::class)
