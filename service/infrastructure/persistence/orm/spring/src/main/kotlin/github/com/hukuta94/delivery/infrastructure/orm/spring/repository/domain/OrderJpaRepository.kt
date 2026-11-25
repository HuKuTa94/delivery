package github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.infrastructure.orm.commons.Orders.Column.COURIER_ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Orders.Column.ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Orders.Column.LOCATION
import github.com.hukuta94.delivery.infrastructure.orm.commons.Orders.Column.STATUS_ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Orders.TABLE_NAME
import github.com.hukuta94.delivery.infrastructure.orm.spring.model.entity.OrderJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface OrderJpaRepository : JpaRepository<OrderJpaEntity, UUID> {

    fun findAllByStatusIs(status: OrderStatus): List<OrderJpaEntity>

    fun findAllByStatusNot(status: OrderStatus): List<OrderJpaEntity>

    /**
     * Inserts a new order unless one with the same id is already stored. A single statement, so callers adding the
     * same order at once don't race: Postgres keeps one row and returns 0 to the others. Unlike `save`, it never
     * merges into the stored order.
     *
     * @return the number of inserted rows: 1 for a new order, 0 for a duplicate
     */
    @Transactional
    @Modifying
    @Query(
        nativeQuery = true,
        value = """
            INSERT INTO $TABLE_NAME ($ID, $STATUS_ID, $COURIER_ID, $LOCATION)
            VALUES (:id, :statusId, :courierId, :location)
            ON CONFLICT ($ID) DO NOTHING
        """,
    )
    fun insertIfAbsent(
        @Param("id") id: UUID,
        @Param("statusId") statusId: Int,
        @Param("courierId") courierId: UUID?,
        @Param("location") location: String,
    ): Int
}
