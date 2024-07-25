package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.jpa

import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.box.OutboxJpaEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OutboxJpaRepository : JpaRepository<OutboxJpaEntity, UUID> {

    fun findAllByProcessedAtIsNull(pageable: Pageable): Page<OutboxJpaEntity>
}