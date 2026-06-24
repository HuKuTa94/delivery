package github.com.hukuta94.delivery.core.application.port.repository.event

import github.com.hukuta94.delivery.core.domain.DomainEvent

class OutboxEventRepositoryFake : OutboxEventRepositoryPort {

    val savedEvents: MutableList<DomainEvent> = mutableListOf()

    override fun saveDomainEvents(domainEvents: Collection<DomainEvent>) {
        savedEvents.addAll(domainEvents)
    }

    fun clear() {
        savedEvents.clear()
    }
}
