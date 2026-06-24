package github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event

import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessage
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import jakarta.persistence.LockModeType
import jakarta.persistence.QueryHint
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.data.repository.NoRepositoryBean
import java.util.*

@NoRepositoryBean
sealed interface SpringJpaBoxEventRepository<
        BOX_EVENT_JPA_ENTITY : BoxEventMessage
> : JpaRepository<BOX_EVENT_JPA_ENTITY, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(
        QueryHint(
            name = "jakarta.persistence.lock.timeout",
            value = "-2", // lock.timeout = -2 is LockOptions.SKIP_LOCKED in Hibernate
        )
    )
    fun findAllByStatusIn(
        statuses: Set<BoxEventMessageStatus>,
        pageable: Pageable,
    ): List<BOX_EVENT_JPA_ENTITY>
}
