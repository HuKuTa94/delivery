package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity

import github.com.hukuta94.delivery.core.domain.order.Order
import github.com.hukuta94.delivery.core.domain.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.sharedkernel.Location
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.LocationConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.OrderStatusConverter
import java.util.*
import javax.persistence.*
import kotlin.reflect.KClass

@Entity
@Table(name = "dlv_order")
class OrderJpaEntity : JpaEntity<Order>() {

    override val domainAggregateClass: KClass<Order>
        get() = Order::class

    @Id
    var id: UUID? = null

    @Column(name = "status")
    @Convert(converter = OrderStatusConverter::class)
    var status: OrderStatus? = null

    @Column(name = "location")
    @Convert(converter = LocationConverter::class)
    var location: Location? = null

    @Column(name = "courier_id")
    var courierId: UUID? = null

    companion object {
        fun fromDomain(domain: Order) = OrderJpaEntity().apply {
            id = domain.id
            status = domain.status
            location = domain.location
            courierId = domain.courierId
        }
    }
}