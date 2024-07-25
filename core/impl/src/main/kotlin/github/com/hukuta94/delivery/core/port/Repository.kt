package github.com.hukuta94.delivery.core.port

import github.com.hukuta94.delivery.core.domain.Aggregate
import java.util.*

interface Repository<AGGREGATE : Aggregate<*>> {

    fun add(aggregate: AGGREGATE)

    fun update(aggregate: AGGREGATE)

    fun update(aggregates: Collection<AGGREGATE>)

    fun getById(id: UUID): AGGREGATE

}