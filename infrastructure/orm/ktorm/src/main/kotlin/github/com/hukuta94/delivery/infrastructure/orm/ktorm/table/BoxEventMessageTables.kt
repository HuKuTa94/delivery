package github.com.hukuta94.delivery.infrastructure.orm.ktorm.table

import github.com.hukuta94.delivery.infrastructure.orm.commons.MessageStatuses
import github.com.hukuta94.delivery.infrastructure.orm.commons.Messages
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.uuid
import org.ktorm.schema.varchar

abstract class BoxEventMessageTable(tableName: String) : Table<Nothing>(tableName) {
    val eventId = uuid(Messages.Column.EVENT_ID).primaryKey()
    val eventType = varchar(Messages.Column.EVENT_TYPE)
    val payload = varchar(Messages.Column.PAYLOAD)
    val createdAt = datetime(Messages.Column.CREATED_AT)
    val processedAt = datetime(Messages.Column.PROCESSED_AT)
    val version = int(Messages.Column.VERSION)
    val statusId = int(Messages.Column.STATUS_ID)
    val errorDescription = varchar(Messages.Column.ERROR_DESCRIPTION)
}

object InboxEventMessageTable : BoxEventMessageTable(Messages.Inbox.TABLE_NAME)
object OutboxEventMessageTable : BoxEventMessageTable(Messages.Outbox.TABLE_NAME)
object BoxEventMessageStatusTable : EnumTable(MessageStatuses.TABLE_NAME)