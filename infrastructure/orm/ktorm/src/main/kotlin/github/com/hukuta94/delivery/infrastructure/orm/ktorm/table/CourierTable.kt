package github.com.hukuta94.delivery.infrastructure.orm.ktorm.table

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.uuid
import org.ktorm.schema.varchar

object CourierTable : Table<Nothing>("couriers") {
    val id = uuid("id").primaryKey()
    val name = varchar("name")
    val location = varchar("location")
    val statusId = int("status_id")
    val transportId = int("transport_id")
}