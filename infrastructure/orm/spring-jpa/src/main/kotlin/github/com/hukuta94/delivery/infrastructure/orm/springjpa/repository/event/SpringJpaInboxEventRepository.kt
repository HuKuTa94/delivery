package github.com.hukuta94.delivery.infrastructure.orm.springjpa.repository.event

import github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.entity.event.InboxEventMessageJpaEntity
import org.springframework.stereotype.Repository

@Repository
interface SpringJpaInboxEventRepository : SpringJpaBoxEventRepository<InboxEventMessageJpaEntity>