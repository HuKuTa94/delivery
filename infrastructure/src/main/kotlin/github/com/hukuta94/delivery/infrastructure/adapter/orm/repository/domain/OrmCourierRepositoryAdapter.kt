package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.domain

import github.com.hukuta94.delivery.core.domain.courier.Courier
import github.com.hukuta94.delivery.core.domain.courier.CourierStatus
import github.com.hukuta94.delivery.core.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.CourierJpaEntity
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.event.OrmOutboxEventRepositoryAdapter
import java.util.*
import kotlin.jvm.optionals.getOrNull

class OrmCourierRepositoryAdapter(
    private val courierJpaRepository: CourierJpaRepository,
    private val outboxRepository: OrmOutboxEventRepositoryAdapter,
) : CourierRepositoryPort {

    override fun add(aggregate: Courier) {
        val jpaEntity = CourierJpaEntity.fromDomain(aggregate)
        courierJpaRepository.save(jpaEntity)
        outboxRepository.saveDomainEvents(aggregate.popDomainEvents())
    }

    override fun update(aggregate: Courier) {
        val jpaEntity = CourierJpaEntity.fromDomain(aggregate)
        courierJpaRepository.save(jpaEntity)
        outboxRepository.saveDomainEvents(aggregate.popDomainEvents())
    }

    override fun update(aggregates: Collection<Courier>) {
        val jpaEntities = aggregates.map {
            CourierJpaEntity.fromDomain(it)
        }
        courierJpaRepository.saveAll(jpaEntities)

        val domainEvents = aggregates.flatMap { it.popDomainEvents() }
        outboxRepository.saveDomainEvents(domainEvents)
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