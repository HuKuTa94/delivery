package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.domain

import github.com.hukuta94.delivery.core.domain.order.OrderStatus
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.OrderJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrderJpaRepository : JpaRepository<OrderJpaEntity, UUID> {

    fun findAllByStatusIs(status: OrderStatus): List<OrderJpaEntity>
}