package github.com.hukuta94.delivery.core.domain

abstract class Aggregate<ID> {

    abstract val id: ID

    private val domainEvents: MutableList<DomainEvent> = mutableListOf()

    fun domainEvents() = domainEvents.toList()

    fun clearDomainEvents() = domainEvents.clear()

    fun raiseDomainEvent(domainEvent: DomainEvent) {
        domainEvents.add(domainEvent)
    }
}