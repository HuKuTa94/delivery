package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository

import github.com.hukuta94.delivery.core.domain.order.Order
import github.com.hukuta94.delivery.core.domain.order.OrderStatus
import github.com.hukuta94.delivery.core.port.OrderRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.OrderJpaEntity
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.box.OrmOutboxRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.jpa.OrderJpaRepository
import java.util.*
import kotlin.jvm.optionals.getOrNull

class OrmOrderRepositoryAdapter(
    private val orderJpaRepository: OrderJpaRepository,
    private val outboxRepository: OrmOutboxRepository,
) : OrderRepository {

    override fun add(aggregate: Order) {
        val jpaEntity = OrderJpaEntity.fromDomain(aggregate)
        orderJpaRepository.save(jpaEntity)
        outboxRepository.saveDomainEvents(aggregate.popDomainEvents())
    }

    override fun update(aggregate: Order) {
        val jpaEntity = OrderJpaEntity.fromDomain(aggregate)
        orderJpaRepository.save(jpaEntity)
        outboxRepository.saveDomainEvents(aggregate.popDomainEvents())
    }

    override fun update(aggregates: Collection<Order>) {
        val jpaEntities = aggregates.map {
            OrderJpaEntity.fromDomain(it)
        }
        orderJpaRepository.saveAll(jpaEntities)

        val domainEvents = aggregates.flatMap { it.popDomainEvents() }
        outboxRepository.saveDomainEvents(domainEvents)
    }

    override fun getById(id: UUID): Order {
        return orderJpaRepository.findById(id)
            .getOrNull()
            ?.toDomain()
            ?: error("The order with id=$id is not found")
    }

    override fun getAllCreated(): Collection<Order> {
        return getAllByStatus(OrderStatus.CREATED)
    }

    override fun getAllAssigned(): Collection<Order> {
        return getAllByStatus(OrderStatus.ASSIGNED)
    }

    override fun getAllNotCompleted(): Collection<Order> {
        return getAllByStatus(OrderStatus.ASSIGNED)
    }

    private fun getAllByStatus(status: OrderStatus): Collection<Order> {
        return orderJpaRepository.findAllByStatusIs(status).map { it.toDomain() }
    }
}