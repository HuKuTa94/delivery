package github.com.hukuta94.delivery.core.application.port.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.Aggregate
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

abstract class AggregateRepositoryFake<AGGREGATE : Aggregate<ID>, ID> : AggregateRepositoryPort<AGGREGATE, ID> {

    /**
     * Ids passed to [update], in call order. The storage keeps references to the aggregates,
     * so it can't show whether a changed aggregate was saved; this list can.
     */
    val updatedIds: MutableList<ID> = CopyOnWriteArrayList()

    protected val storage = ConcurrentHashMap<ID, AGGREGATE>()

    override fun add(aggregate: AGGREGATE): Boolean = storage.putIfAbsent(aggregate.id, aggregate) == null

    override fun update(aggregate: AGGREGATE) {
        storage[aggregate.id] = aggregate
        updatedIds.add(aggregate.id)
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
     * Remove all stored aggregates and recorded updates in fake repository
     */
    fun clear() {
        storage.clear()
        updatedIds.clear()
    }
}
