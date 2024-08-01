package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.event

import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event.OutboxEventJpaEntity
import org.springframework.stereotype.Repository

@Repository
interface OutboxEventJpaRepository : BoxEventJpaRepository<OutboxEventJpaEntity>