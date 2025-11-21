package github.com.hukuta94.delivery.core.application.port.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.Aggregate

interface AggregateRepositoryPort<AGGREGATE : Aggregate<ID>, ID> {

    fun add(aggregate: AGGREGATE)

    fun update(aggregate: AGGREGATE)

    fun update(aggregates: Collection<AGGREGATE>)

    fun getById(id: ID): AGGREGATE

    fun existsById(id: ID): Boolean
}
