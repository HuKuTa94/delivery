package github.com.hukuta94.delivery.core.domain

abstract class Aggregate<ID> {

    abstract val id: ID

    private val domainEvents: MutableList<DomainEvent> = mutableListOf()

    fun popDomainEvents(): List<DomainEvent> {
        val popEvents = domainEvents.toList()
        domainEvents.clear()
        return popEvents
    }

    fun raiseDomainEvent(domainEvent: DomainEvent) {
        domainEvents.add(domainEvent)
    }
}