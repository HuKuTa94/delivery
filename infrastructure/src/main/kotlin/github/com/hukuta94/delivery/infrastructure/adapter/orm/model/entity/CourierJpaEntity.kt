package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity

import github.com.hukuta94.delivery.core.domain.courier.Courier
import github.com.hukuta94.delivery.core.domain.courier.CourierName
import github.com.hukuta94.delivery.core.domain.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.courier.Transport
import github.com.hukuta94.delivery.core.domain.sharedkernel.Location
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.CourierNameConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.CourierStatusConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.LocationConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.TransportConverter
import java.util.*
import javax.persistence.*
import kotlin.reflect.KClass

@Entity
@Table(name = "dlv_courier")
class CourierJpaEntity : JpaEntity<Courier>() {

    override val domainAggregateClass: KClass<Courier>
        get() = Courier::class

    @Id
    var id: UUID? = null

    @Column(name = "name")
    @Convert(converter = CourierNameConverter::class)
    var name: CourierName? = null

    @Column(name = "location")
    @Convert(converter = LocationConverter::class)
    var location: Location? = null

    @Column(name = "transport")
    @Convert(converter = TransportConverter::class)
    var transport: Transport? = null

    @Column(name = "status")
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