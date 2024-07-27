package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.box

import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.box.InboxJpaEntity.Companion.TABLE_NAME
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = TABLE_NAME)
class InboxJpaEntity {

    @Id
    lateinit var id: UUID

    @Column(name = "type")
    lateinit var type: String

    @Column(name = "content")
    lateinit var content: String

    @Column(name = "created_at")
    lateinit var createdAt: LocalDateTime

    @Column(name = "processed_at")
    var processedAt: LocalDateTime? = null

    companion object {
        const val TABLE_NAME = "dlv_inbox"
    }
}