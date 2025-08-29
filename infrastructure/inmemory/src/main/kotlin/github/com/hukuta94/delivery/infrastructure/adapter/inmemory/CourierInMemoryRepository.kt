package github.com.hukuta94.delivery.infrastructure.adapter.inmemory

import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import java.util.*

class CourierInMemoryRepository : CourierRepositoryPort {

    private val storage = mutableMapOf<UUID, Courier>()

    override fun add(aggregate: Courier) {
        storage[aggregate.id] = aggregate
    }

    override fun update(aggregate: Courier) {
        storage[aggregate.id] = aggregate
    }

    override fun update(aggregates: Collection<Courier>) {
        aggregates.forEach { aggregate ->
            update(aggregate)
        }
    }

    override fun getById(id: UUID): Courier {
        return storage[id] ?: error("The courier with id=$id is not found")
    }

    override fun existsById(id: UUID): Boolean {
        return storage.containsKey(id)
    }

    override fun getAllFree(): Collection<Courier> {
        return storage.values.filter { courier ->
            courier.status == CourierStatus.FREE
        }
    }

    override fun getAllBusy(): Collection<Courier> {
        return storage.values.filter { courier ->
            courier.status == CourierStatus.BUSY
        }
    }
}