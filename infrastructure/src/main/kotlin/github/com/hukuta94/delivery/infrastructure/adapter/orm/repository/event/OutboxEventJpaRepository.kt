package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.event

import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event.OutboxEventJpaEntity
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event.OutboxEventStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface OutboxEventJpaRepository : BoxEventJpaRepository<OutboxEventJpaEntity> {

    fun findAllByStatusIn(statuses: Set<OutboxEventStatus>, pageable: Pageable): Page<OutboxEventJpaEntity>
}