package github.com.hukuta94.delivery.infrastructure.orm.ktorm.table

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

object CourierStatusTable : Table<Nothing>("courier_statuses") {
    val id = int("id").primaryKey()
    val code = varchar("code")
}