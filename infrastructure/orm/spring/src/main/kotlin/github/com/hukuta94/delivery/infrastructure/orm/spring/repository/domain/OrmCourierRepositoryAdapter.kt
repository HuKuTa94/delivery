package github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.infrastructure.orm.spring.model.entity.CourierJpaEntity
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event.SpringJpaOutboxEventRepositoryAdapter
import java.util.*
import kotlin.jvm.optionals.getOrNull

class OrmCourierRepositoryAdapter(
    private val courierJpaRepository: CourierJpaRepository,
    private val outboxRepository: SpringJpaOutboxEventRepositoryAdapter,
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

    override fun existsById(id: UUID): Boolean {
        return courierJpaRepository.existsById(id)
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
