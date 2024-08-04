package github.com.hukuta94.delivery.core.application.port.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.Aggregate
import java.util.*

sealed interface AggregateRepository<AGGREGATE : Aggregate<*>> {

    fun add(aggregate: AGGREGATE)

    fun update(aggregate: AGGREGATE)

    fun update(aggregates: Collection<AGGREGATE>)

    fun getById(id: UUID): AGGREGATE

    fun existsById(id: UUID): Boolean

}