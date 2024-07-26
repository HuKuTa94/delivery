package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.DomainEvent

interface DomainEventPublisher {

    fun publish(domainEvent: DomainEvent)
}