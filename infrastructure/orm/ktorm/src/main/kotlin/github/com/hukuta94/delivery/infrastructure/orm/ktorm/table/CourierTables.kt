package github.com.hukuta94.delivery.infrastructure.orm.ktorm.table

import github.com.hukuta94.delivery.infrastructure.orm.commons.CourierStatuses
import github.com.hukuta94.delivery.infrastructure.orm.commons.CourierTransports
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.uuid
import org.ktorm.schema.varchar

object CourierTable : Table<Nothing>(Couriers.TABLE_NAME) {
    val id = uuid(Couriers.Column.ID).primaryKey()
    val name = varchar(Couriers.Column.NAME)
    val location = varchar(Couriers.Column.LOCATION)
    val statusId = int(Couriers.Column.STATUS_ID)
    val transportId = int(Couriers.Column.TRANSPORT_ID)
}

object CourierStatusTable : EnumTable(CourierStatuses.TABLE_NAME)
object CourierTransportTable : EnumTable(CourierTransports.TABLE_NAME)