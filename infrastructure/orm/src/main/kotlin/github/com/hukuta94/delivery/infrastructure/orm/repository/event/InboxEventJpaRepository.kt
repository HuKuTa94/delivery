package github.com.hukuta94.delivery.infrastructure.orm.repository.event

import github.com.hukuta94.delivery.infrastructure.orm.model.entity.event.InboxEventJpaEntity
import org.springframework.stereotype.Repository

@Repository
interface InboxEventJpaRepository : BoxEventJpaRepository<InboxEventJpaEntity>