package github.com.hukuta94.delivery.core.port.repository.event

import github.com.hukuta94.delivery.core.domain.DomainEvent

interface OutboxEventRepositoryPort {

    fun saveDomainEvents(domainEvents: Collection<DomainEvent>)
}