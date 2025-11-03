package github.com.hukuta94.delivery.infrastructure.orm.ktorm.table

import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.int
import org.ktorm.schema.uuid
import org.ktorm.schema.varchar

object InboxEventMessageTable : Table<Nothing>("in_box") {
    val id = uuid("id").primaryKey()
    val eventType = varchar("event_type")
    val payload = varchar("payload")
    val createdAt = datetime("created_at")
    val processedAt = datetime("processed_at")
    val version = int("version")
    val statusId = int("status_id")
    val errorDescription = varchar("error_description")
}