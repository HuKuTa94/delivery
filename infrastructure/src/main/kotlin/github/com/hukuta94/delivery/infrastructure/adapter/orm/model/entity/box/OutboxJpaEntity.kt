package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.box

import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.box.OutboxJpaEntity.Companion.TABLE_NAME
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = TABLE_NAME)
class OutboxJpaEntity {

    @Id
    var id: UUID? = null

    @Column(name = "type")
    var type: String? = null

    @Column(name = "content")
    var content: String? = null

    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null

    @Column(name = "processed_at")
    var processedAt: LocalDateTime? = null

    companion object {
        const val TABLE_NAME = "dlv_outbox"
    }
}