package github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.order.Order
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryPort
import github.com.hukuta94.delivery.infrastructure.orm.commons.toDb
import github.com.hukuta94.delivery.infrastructure.orm.spring.model.entity.OrderJpaEntity
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

class OrmOrderRepositoryAdapter(
    private val orderJpaRepository: OrderJpaRepository,
) : OrderRepositoryPort {

    override fun add(aggregate: Order) {
        val insertedRows = orderJpaRepository.insertIfAbsent(
            id = aggregate.id,
            statusId = aggregate.status.id,
            courierId = aggregate.courierId,
            location = aggregate.location.toDb(),
        )

        if (insertedRows == 0) {
            LOG.info("Order with id: ${aggregate.id} skipped because it already exists")
        }
    }

    override fun update(aggregate: Order) {
        val jpaEntity = OrderJpaEntity.fromDomain(aggregate)
        orderJpaRepository.save(jpaEntity)
    }

    override fun update(aggregates: Collection<Order>) {
        val jpaEntities = aggregates.map {
            OrderJpaEntity.fromDomain(it)
        }
        orderJpaRepository.saveAll(jpaEntities)
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
        return orderJpaRepository.findAllByStatusNot(OrderStatus.COMPLETED).map { it.toDomain() }
    }

    private fun getAllByStatus(status: OrderStatus): Collection<Order> {
        return orderJpaRepository.findAllByStatusIs(status).map { it.toDomain() }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(OrmOrderRepositoryAdapter::class.java)
    }
}
