package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository

import github.com.hukuta94.delivery.core.domain.courier.Courier
import github.com.hukuta94.delivery.core.domain.courier.CourierStatus
import github.com.hukuta94.delivery.core.port.CourierRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.CourierJpaEntity
import java.util.*
import kotlin.jvm.optionals.getOrNull

class OrmCourierRepositoryAdapter(
    private val courierJpaRepository: CourierJpaRepository,
) : CourierRepository {

    override fun add(aggregate: Courier) {
        val jpaEntity = CourierJpaEntity.fromDomain(aggregate)
        courierJpaRepository.save(jpaEntity)
    }

    override fun update(aggregate: Courier) {
        val jpaEntity = CourierJpaEntity.fromDomain(aggregate)
        courierJpaRepository.save(jpaEntity)
    }

    override fun update(aggregates: Collection<Courier>) {
        val jpaEntities = aggregates.map {
            CourierJpaEntity.fromDomain(it)
        }
        courierJpaRepository.saveAll(jpaEntities)
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