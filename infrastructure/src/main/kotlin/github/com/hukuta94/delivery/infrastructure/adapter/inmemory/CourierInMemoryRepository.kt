package github.com.hukuta94.delivery.infrastructure.adapter.inmemory

import github.com.hukuta94.delivery.core.application.event.DomainEventPublisher
import github.com.hukuta94.delivery.core.domain.courier.Courier
import github.com.hukuta94.delivery.core.domain.courier.CourierName
import github.com.hukuta94.delivery.core.domain.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.courier.Transport
import github.com.hukuta94.delivery.core.domain.sharedkernel.Location
import github.com.hukuta94.delivery.core.port.CourierRepository
import java.util.UUID

class CourierInMemoryRepository(
    domainEventPublisher: DomainEventPublisher,
) : CourierRepository(domainEventPublisher) {

    private val storage = mutableMapOf<UUID, Courier>().also {
        val courierId = UUID.randomUUID()
        it[courierId] = Courier.create(
            name = CourierName("Доставщик Петя"),
            transport = Transport.PEDESTRIAN,
            location = Location.minimal()
        )
    }

    override fun add(domainEntity: Courier) {
        publishDomainEvents(domainEntity)
        storage[domainEntity.id] = domainEntity
    }

    override fun update(domainEntity: Courier) {
        publishDomainEvents(domainEntity)
        storage[domainEntity.id] = domainEntity
    }

    override fun update(domainEntities: Collection<Courier>) {
        domainEntities.forEach { domainEntity ->
            update(domainEntity)
        }
    }

    override fun getById(id: UUID): Courier {
        return storage[id] ?: error("The courier with id=$id is not found")
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