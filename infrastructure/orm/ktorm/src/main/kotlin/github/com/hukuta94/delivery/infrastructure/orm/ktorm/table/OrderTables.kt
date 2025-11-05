package github.com.hukuta94.delivery.infrastructure.orm.ktorm.table

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.uuid
import org.ktorm.schema.varchar

object OrderTable : Table<Nothing>("orders") {
    val id = uuid("id").primaryKey()
    val statusId = int("status_id")
    val courierId = uuid("courier_id")
    val location = varchar("location")
}

object OrderStatusTable : EnumTable("order_statuses")