package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.event

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.util.*

@NoRepositoryBean
sealed interface BoxEventJpaRepository<BOX_JPA_ENTITY> : JpaRepository<BOX_JPA_ENTITY, UUID> {

    fun findAllByProcessedAtIsNull(pageable: Pageable): Page<BOX_JPA_ENTITY>
}