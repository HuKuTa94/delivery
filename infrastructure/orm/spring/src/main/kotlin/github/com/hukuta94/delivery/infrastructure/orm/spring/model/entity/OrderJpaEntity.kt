package github.com.hukuta94.delivery.infrastructure.orm.spring.model.entity

import github.com.hukuta94.delivery.core.domain.aggregate.order.Order
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.common.Location
import github.com.hukuta94.delivery.infrastructure.orm.commons.Orders.Column.COURIER_ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Orders.Column.ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Orders.Column.LOCATION
import github.com.hukuta94.delivery.infrastructure.orm.commons.Orders.Column.STATUS_ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Orders.TABLE_NAME
import github.com.hukuta94.delivery.infrastructure.orm.spring.model.converter.LocationConverter
import github.com.hukuta94.delivery.infrastructure.orm.spring.model.converter.OrderStatusConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID
import kotlin.reflect.KClass

@Entity
@Table(name = TABLE_NAME)
class OrderJpaEntity : JpaEntity<Order>() {

    override val domainAggregateClass: KClass<Order>
        get() = Order::class

    @Id
    @Column(name = ID)
    var id: UUID? = null

    @Column(name = STATUS_ID)
    @Convert(converter = OrderStatusConverter::class)
    var status: OrderStatus? = null

    @Column(name = LOCATION)
    @Convert(converter = LocationConverter::class)
    var location: Location? = null

    @Column(name = COURIER_ID)
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
