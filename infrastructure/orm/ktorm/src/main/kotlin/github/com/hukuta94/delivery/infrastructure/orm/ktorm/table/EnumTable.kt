package github.com.hukuta94.delivery.infrastructure.orm.ktorm.table

import github.com.hukuta94.delivery.infrastructure.orm.commons.EnumColumn
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.varchar

abstract class EnumTable(tableName: String) : Table<Nothing>(tableName)  {
    val id = int(EnumColumn.ID).primaryKey()
    val code = varchar(EnumColumn.CODE)
}