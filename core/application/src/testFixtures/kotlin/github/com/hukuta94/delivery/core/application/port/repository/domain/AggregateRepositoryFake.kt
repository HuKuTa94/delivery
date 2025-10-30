package github.com.hukuta94.delivery.core.application.port.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.Aggregate
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

open class AggregateRepositoryFake<AGGREGATE : Aggregate<*>> : AggregateRepositoryPort<AGGREGATE> {

    protected val storage = ConcurrentHashMap<UUID, AGGREGATE>()

    override fun add(aggregate: AGGREGATE) {
        storage[aggregate.id as UUID] = aggregate
    }

    override fun update(aggregate: AGGREGATE) {
        storage[aggregate.id as UUID] = aggregate
    }

    override fun update(aggregates: Collection<AGGREGATE>) {
        aggregates.forEach { aggregate -> update(aggregate) }
    }

    override fun getById(id: UUID): AGGREGATE {
        return storage[id] ?: error("The courier with id=$id is not found")
    }

    override fun existsById(id: UUID): Boolean {
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