package github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event

import github.com.hukuta94.delivery.infrastructure.orm.spring.model.entity.event.InboxEventMessageJpaEntity
import org.springframework.stereotype.Repository

@Repository
interface SpringJpaInboxEventRepository : SpringJpaBoxEventRepository<InboxEventMessageJpaEntity>
