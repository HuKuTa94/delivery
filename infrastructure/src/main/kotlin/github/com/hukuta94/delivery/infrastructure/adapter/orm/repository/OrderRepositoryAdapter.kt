package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository

import github.com.hukuta94.delivery.core.application.event.DomainEventPublisher
import github.com.hukuta94.delivery.core.domain.order.Order
import github.com.hukuta94.delivery.core.domain.order.OrderStatus
import github.com.hukuta94.delivery.core.port.OrderRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.OrderJpaEntity
import java.util.*
import kotlin.jvm.optionals.getOrNull

class OrderRepositoryAdapter(
    domainEventPublisher: DomainEventPublisher,
    private val orderJpaRepository: OrderJpaRepository,
) : OrderRepository(domainEventPublisher) {

    override fun add(domainEntity: Order) {
        val jpaEntity = OrderJpaEntity.fromDomain(domainEntity)
        orderJpaRepository.saveAndFlush(jpaEntity)
    }

    override fun update(domainEntity: Order) {
        val jpaEntity = OrderJpaEntity.fromDomain(domainEntity)
        orderJpaRepository.saveAndFlush(jpaEntity)
    }

    override fun update(domainEntities: Collection<Order>) {
        val jpaEntities = domainEntities.map {
            OrderJpaEntity.fromDomain(it)
        }
        orderJpaRepository.saveAllAndFlush(jpaEntities)
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