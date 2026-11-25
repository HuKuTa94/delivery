package github.com.hukuta94.delivery.infrastructure.orm.spring.model.entity.event

import github.com.hukuta94.delivery.infrastructure.orm.commons.Messages.Inbox.TABLE_NAME
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = TABLE_NAME)
class InboxEventMessageJpaEntity : BoxEventMessageJpaEntity()
