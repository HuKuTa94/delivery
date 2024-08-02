package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.event

import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event.InboxEventJpaEntity
import org.springframework.stereotype.Repository

@Repository
interface InboxEventJpaRepository : BoxEventJpaRepository<InboxEventJpaEntity>