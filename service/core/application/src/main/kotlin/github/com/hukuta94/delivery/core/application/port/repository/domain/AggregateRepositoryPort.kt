package github.com.hukuta94.delivery.core.application.port.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.Aggregate

interface AggregateRepositoryPort<AGGREGATE : Aggregate<ID>, ID> {

    /**
     * Stores a new aggregate unless one with the same id is already stored; the stored one is kept as it is.
     *
     * @return `true` if the aggregate was stored, `false` if one with the same id already exists
     */
    fun add(aggregate: AGGREGATE): Boolean

    fun update(aggregate: AGGREGATE)

    fun update(aggregates: Collection<AGGREGATE>)

    fun getById(id: ID): AGGREGATE

    fun existsById(id: ID): Boolean
}
