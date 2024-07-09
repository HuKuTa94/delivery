package github.com.hukuta94.delivery.infrastructure.adapter.inmemory

import github.com.hukuta94.delivery.core.domain.courier.Courier
import github.com.hukuta94.delivery.core.domain.courier.CourierStatus
import github.com.hukuta94.delivery.core.port.CourierRepository
import java.util.UUID

class CourierInMemoryRepository : CourierRepository {

    private val storage = mutableMapOf<UUID, Courier>()

    override fun add(courier: Courier) {
        storage[courier.id] = courier
    }

    override fun update(courier: Courier) {
        storage[courier.id] = courier
    }

    override fun update(couriers: List<Courier>) {
        couriers.forEach { courier ->
            storage[courier.id] = courier
        }
    }

    override fun getById(id: UUID): Courier {
        return storage[id] ?: error("The courier with id=$id is not found")
    }

    override fun getAllFree(): List<Courier> {
        return storage.values.filter { courier ->
            courier.status == CourierStatus.FREE
        }
    }

    override fun getAllBusy(): List<Courier> {
        return storage.values.filter { courier ->
            courier.status == CourierStatus.BUSY
        }
    }
}