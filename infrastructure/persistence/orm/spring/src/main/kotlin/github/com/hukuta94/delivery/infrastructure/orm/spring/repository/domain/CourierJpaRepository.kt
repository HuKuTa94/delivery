package github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.Column.ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.Column.LOCATION
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.Column.NAME
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.Column.STATUS_ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.Column.TRANSPORT_ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.TABLE_NAME
import github.com.hukuta94.delivery.infrastructure.orm.spring.model.entity.CourierJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Repository
interface CourierJpaRepository : JpaRepository<CourierJpaEntity, UUID> {

    fun findAllByStatusIs(status: CourierStatus): List<CourierJpaEntity>

    /**
     * Inserts a new courier unless one with the same id is already stored. A single statement, so callers adding the
     * same courier at once don't race: Postgres keeps one row and returns 0 to the others. Unlike `save`, it never
     * merges into the stored courier.
     *
     * @return the number of inserted rows: 1 for a new courier, 0 for a duplicate
     */
    @Transactional
    @Modifying
    @Query(
        nativeQuery = true,
        value = """
            INSERT INTO $TABLE_NAME ($ID, $NAME, $LOCATION, $STATUS_ID, $TRANSPORT_ID)
            VALUES (:id, :name, :location, :statusId, :transportId)
            ON CONFLICT ($ID) DO NOTHING
        """,
    )
    fun insertIfAbsent(
        @Param("id") id: UUID,
        @Param("name") name: String,
        @Param("location") location: String,
        @Param("statusId") statusId: Int,
        @Param("transportId") transportId: Int,
    ): Int
}
