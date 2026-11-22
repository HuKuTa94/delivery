package github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event

import github.com.hukuta94.delivery.infrastructure.orm.commons.Messages.Column.CREATED_AT
import github.com.hukuta94.delivery.infrastructure.orm.commons.Messages.Column.EVENT_ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Messages.Column.EVENT_TYPE
import github.com.hukuta94.delivery.infrastructure.orm.commons.Messages.Column.PAYLOAD
import github.com.hukuta94.delivery.infrastructure.orm.commons.Messages.Column.STATUS_ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Messages.Column.VERSION
import github.com.hukuta94.delivery.infrastructure.orm.commons.Messages.Inbox.TABLE_NAME
import github.com.hukuta94.delivery.infrastructure.orm.spring.model.entity.event.InboxEventMessageJpaEntity
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface SpringJpaInboxEventRepository : SpringJpaBoxEventRepository<InboxEventMessageJpaEntity> {

    /**
     * Inserts a new message unless one with the same event id is already stored. A single statement, so callers
     * saving the same event at once don't race: Postgres keeps one row and returns 0 to the others.
     *
     * @return the number of inserted rows: 1 for a new event, 0 for a duplicate
     */
    @Transactional
    @Modifying
    @Query(
        nativeQuery = true,
        value = """
            INSERT INTO $TABLE_NAME ($EVENT_ID, $EVENT_TYPE, $PAYLOAD, $CREATED_AT, $VERSION, $STATUS_ID)
            VALUES (:eventId, :eventType, :payload, :createdAt, 0, :statusId)
            ON CONFLICT ($EVENT_ID) DO NOTHING
        """,
    )
    fun insertIfAbsent(
        @Param("eventId") eventId: UUID,
        @Param("eventType") eventType: String,
        @Param("payload") payload: String,
        @Param("createdAt") createdAt: LocalDateTime,
        @Param("statusId") statusId: Int,
    ): Int
}
