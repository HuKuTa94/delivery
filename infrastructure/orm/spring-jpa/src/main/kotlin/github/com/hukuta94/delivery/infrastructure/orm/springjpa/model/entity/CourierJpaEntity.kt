package github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.entity

import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierName
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Transport
import github.com.hukuta94.delivery.core.domain.common.Location
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.Column.ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.Column.LOCATION
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.Column.NAME
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.Column.STATUS_ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.Column.TRANSPORT_ID
import github.com.hukuta94.delivery.infrastructure.orm.commons.Couriers.TABLE_NAME
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.converter.CourierNameConverter
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.converter.CourierStatusConverter
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.converter.LocationConverter
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.converter.TransportConverter
import java.util.*
import jakarta.persistence.*
import kotlin.reflect.KClass

@Entity
@Table(name = TABLE_NAME)
class CourierJpaEntity : JpaEntity<Courier>() {

    override val domainAggregateClass: KClass<Courier>
        get() = Courier::class

    @Id
    @Column(name = ID)
    var id: UUID? = null

    @Column(name = NAME)
    @Convert(converter = CourierNameConverter::class)
    var name: CourierName? = null

    @Column(name = LOCATION)
    @Convert(converter = LocationConverter::class)
    var location: Location? = null

    @Column(name = TRANSPORT_ID)
    @Convert(converter = TransportConverter::class)
    var transport: Transport? = null

    @Column(name = STATUS_ID)
    @Convert(converter = CourierStatusConverter::class)
    var status: CourierStatus? = null

    companion object {

        fun fromDomain(domain: Courier) = CourierJpaEntity().apply {
            id = domain.id
            name = domain.name
            location = domain.location
            transport = domain.transport
            status = domain.status
        }
    }
}