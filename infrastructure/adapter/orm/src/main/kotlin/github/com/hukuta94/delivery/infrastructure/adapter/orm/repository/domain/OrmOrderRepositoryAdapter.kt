package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.order.Order
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryPort
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.OrderJpaEntity
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.event.OrmOutboxEventRepositoryAdapter
import java.util.*
import kotlin.jvm.optionals.getOrNull

class OrmOrderRepositoryAdapter(
    private val orderJpaRepository: OrderJpaRepository,
    private val outboxRepository: OrmOutboxEventRepositoryAdapter,
) : OrderRepositoryPort {

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

    override fun existsById(id: UUID): Boolean {
        return orderJpaRepository.existsById(id)
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