package github.com.hukuta94.delivery.core.port

import github.com.hukuta94.delivery.core.application.event.DomainEventPublisher
import github.com.hukuta94.delivery.core.domain.Aggregate
import java.util.*

abstract class Repository<AGGREGATE : Aggregate<*>> (
    private val domainEventPublisher: DomainEventPublisher,
) {

    abstract fun add(aggregate: AGGREGATE)

    abstract fun update(aggregate: AGGREGATE)

    abstract fun update(aggregates: Collection<AGGREGATE>)

    abstract fun getById(id: UUID): AGGREGATE

    //TODO Здесь (в будущем) будем только сохранять события в таблицу dlv_outbox
    // а публиковать события будет отдельная джоба
    /**
     * Must be called before save entity state in storage
     */
    protected fun publishDomainEvents(aggregates: Collection<AGGREGATE>) {
        val domainEvents = aggregates.flatMap { it.domainEvents() }
        aggregates.forEach { it.clearDomainEvents() }
        domainEventPublisher.publish(domainEvents)
    }

    /**
     * Must be called before save entity state in storage
     */
    protected fun publishDomainEvents(aggregate: AGGREGATE) {
        val domainEvents = aggregate.domainEvents()
        aggregate.clearDomainEvents()
        domainEventPublisher.publish(domainEvents)
    }
}