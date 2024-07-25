package github.com.hukuta94.delivery.core.port

import github.com.hukuta94.delivery.core.domain.Aggregate
import java.util.*

abstract class Repository<AGGREGATE : Aggregate<*>> {

    abstract fun add(aggregate: AGGREGATE)

    abstract fun update(aggregate: AGGREGATE)

    abstract fun update(aggregates: Collection<AGGREGATE>)

    abstract fun getById(id: UUID): AGGREGATE

}