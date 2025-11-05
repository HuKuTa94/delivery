package github.com.hukuta94.delivery.infrastructure.orm.ktorm.table

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

abstract class EnumTable(tableName: String) : Table<Nothing>(tableName)  {
    val id = int("id").primaryKey()
    val code = varchar("code")
}