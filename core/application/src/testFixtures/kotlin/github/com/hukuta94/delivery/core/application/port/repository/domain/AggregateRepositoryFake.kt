package github.com.hukuta94.delivery.core.application.port.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.Aggregate
import java.util.concurrent.ConcurrentHashMap

abstract class AggregateRepositoryFake<AGGREGATE : Aggregate<ID>, ID> : AggregateRepositoryPort<AGGREGATE, ID> {

    protected val storage = ConcurrentHashMap<ID, AGGREGATE>()

    override fun add(aggregate: AGGREGATE) {
        storage[aggregate.id] = aggregate
    }

    override fun update(aggregate: AGGREGATE) {
        storage[aggregate.id] = aggregate
    }

    override fun update(aggregates: Collection<AGGREGATE>) {
        aggregates.forEach { aggregate -> update(aggregate) }
    }

    override fun getById(id: ID): AGGREGATE {
        return storage[id] ?: error("The courier with id=$id is not found")
    }

    override fun existsById(id: ID): Boolean {
        return storage.containsKey(id)
    }

    fun addAll(aggregates: Collection<AGGREGATE>) {
        aggregates.forEach { add(it) }
    }

    /**
     * Remove all stored aggregates in fake repository
     */
    fun clear() = storage.clear()
}