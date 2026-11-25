package github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event

import github.com.hukuta94.delivery.infrastructure.orm.spring.model.entity.event.OutboxEventMessageJpaEntity
import org.springframework.stereotype.Repository

@Repository
interface SpringJpaOutboxEventRepository : SpringJpaBoxEventRepository<OutboxEventMessageJpaEntity>
