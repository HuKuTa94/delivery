package github.com.hukuta94.delivery.infrastructure.adapter.inmemory

import github.com.hukuta94.delivery.core.domain.courier.Courier
import github.com.hukuta94.delivery.core.domain.courier.CourierName
import github.com.hukuta94.delivery.core.domain.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.courier.Transport
import github.com.hukuta94.delivery.core.domain.common.Location
import github.com.hukuta94.delivery.core.port.repository.domain.CourierRepositoryPort
import java.util.*

class CourierInMemoryRepository : CourierRepositoryPort {

    private val storage = mutableMapOf<UUID, Courier>().also {
        val courierId = UUID.randomUUID()
        it[courierId] = Courier.create(
            name = CourierName("Доставщик Петя"),
            transport = Transport.PEDESTRIAN,
            location = Location.minimal()
        )
    }

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