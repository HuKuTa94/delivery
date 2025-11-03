package github.com.hukuta94.delivery.infrastructure.orm.springjpa.repository.event

import github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.entity.event.OutboxEventMessageJpaEntity
import org.springframework.stereotype.Repository

@Repository
interface SpringJpaOutboxEventRepository : SpringJpaBoxEventRepository<OutboxEventMessageJpaEntity>