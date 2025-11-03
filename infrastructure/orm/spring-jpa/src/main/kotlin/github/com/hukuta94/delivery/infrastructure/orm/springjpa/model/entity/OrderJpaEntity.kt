package github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.entity

import github.com.hukuta94.delivery.core.domain.aggregate.order.Order
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.common.Location
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.converter.LocationConverter
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.converter.OrderStatusConverter
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.entity.OrderJpaEntity.Companion.TABLE_NAME
import java.util.*
import jakarta.persistence.*
import kotlin.reflect.KClass

@Entity
@Table(name = TABLE_NAME)
class OrderJpaEntity : JpaEntity<Order>() {

    override val domainAggregateClass: KClass<Order>
        get() = Order::class

    @Id
    var id: UUID? = null

    @Column(name = STATUS_ID)
    @Convert(converter = OrderStatusConverter::class)
    var status: OrderStatus? = null

    @Column(name = "location")
    @Convert(converter = LocationConverter::class)
    var location: Location? = null

    @Column(name = COURIER_ID)
    var courierId: UUID? = null

    companion object {
        const val TABLE_NAME = "orders"
        const val STATUS_ID = "status_id"
        const val COURIER_ID = "courier_id"

        fun fromDomain(domain: Order) = OrderJpaEntity().apply {
            id = domain.id
            status = domain.status
            location = domain.location
            courierId = domain.courierId
        }
    }
}