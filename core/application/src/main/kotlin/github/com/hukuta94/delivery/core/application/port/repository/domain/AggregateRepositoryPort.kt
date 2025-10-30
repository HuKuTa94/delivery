package github.com.hukuta94.delivery.core.application.port.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.Aggregate
import java.util.*

interface AggregateRepositoryPort<AGGREGATE : Aggregate<*>> {

    fun add(aggregate: AGGREGATE)

    fun update(aggregate: AGGREGATE)

    fun update(aggregates: Collection<AGGREGATE>)

    //TODO вынести UUID в generic интерфейса
    fun getById(id: UUID): AGGREGATE

    fun existsById(id: UUID): Boolean

}