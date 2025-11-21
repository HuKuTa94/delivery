package github.com.hukuta94.delivery.infrastructure.orm.ktorm.table

import github.com.hukuta94.delivery.infrastructure.orm.commons.OrderStatuses
import github.com.hukuta94.delivery.infrastructure.orm.commons.Orders
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.uuid
import org.ktorm.schema.varchar

object OrderTable : Table<Nothing>(Orders.TABLE_NAME) {
    val id = uuid(Orders.Column.ID).primaryKey()
    val statusId = int(Orders.Column.STATUS_ID)
    val courierId = uuid(Orders.Column.COURIER_ID)
    val location = varchar(Orders.Column.LOCATION)
}

object OrderStatusTable : EnumTable(OrderStatuses.TABLE_NAME)
