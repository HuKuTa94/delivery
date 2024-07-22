package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository

import github.com.hukuta94.delivery.core.application.event.DomainEventPublisher
import github.com.hukuta94.delivery.core.domain.courier.Courier
import github.com.hukuta94.delivery.core.domain.courier.CourierStatus
import github.com.hukuta94.delivery.core.port.CourierRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.CourierJpaEntity
import java.util.*
import kotlin.jvm.optionals.getOrNull

class CourierRepositoryAdapter(
    domainEventPublisher: DomainEventPublisher,
    private val courierJpaRepository: CourierJpaRepository,
) : CourierRepository(domainEventPublisher) {

    override fun add(domainEntity: Courier) {
        val jpaEntity = CourierJpaEntity.fromDomain(domainEntity)
        courierJpaRepository.saveAndFlush(jpaEntity)
    }

    override fun update(domainEntity: Courier) {
        val jpaEntity = CourierJpaEntity.fromDomain(domainEntity)
        courierJpaRepository.saveAndFlush(jpaEntity)
    }

    override fun update(domainEntities: Collection<Courier>) {
        val jpaEntities = domainEntities.map {
            CourierJpaEntity.fromDomain(it)
        }
        courierJpaRepository.saveAllAndFlush(jpaEntities)
    }

    override fun getById(id: UUID): Courier {
        return courierJpaRepository.findById(id)
            .getOrNull()
            ?.toDomain()
            ?: error("The courier with id=$id is not found")
    }

    override fun getAllFree(): Collection<Courier> {
        return getAllByStatus(CourierStatus.FREE)
    }

    override fun getAllBusy(): Collection<Courier> {
        return getAllByStatus(CourierStatus.BUSY)
    }

    private fun getAllByStatus(status: CourierStatus): Collection<Courier> {
        return courierJpaRepository.findAllByStatusIs(status).map { it.toDomain() }
    }
}