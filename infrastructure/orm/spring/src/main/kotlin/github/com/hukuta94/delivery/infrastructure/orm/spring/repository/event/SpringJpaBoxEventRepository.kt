package github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event

import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessage
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.util.*

@NoRepositoryBean
sealed interface SpringJpaBoxEventRepository<
        BOX_EVENT_JPA_ENTITY : BoxEventMessage
> : JpaRepository<BOX_EVENT_JPA_ENTITY, UUID> {

    fun findAllByStatusIn(
        statuses: Set<BoxEventMessageStatus>,
        pageable: Pageable,
    ): Page<out BOX_EVENT_JPA_ENTITY>
}
