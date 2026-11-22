package github.com.hukuta94.delivery.infrastructure.orm.spring.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.infrastructure.orm.spring.model.entity.CourierJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CourierJpaRepository : JpaRepository<CourierJpaEntity, UUID> {

    fun findAllByStatusIs(status: CourierStatus): List<CourierJpaEntity>
}
