package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity

import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierName
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Transport
import github.com.hukuta94.delivery.core.domain.common.Location
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.CourierNameConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.CourierStatusConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.LocationConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.TransportConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.CourierJpaEntity.Companion.TABLE_NAME
import java.util.*
import jakarta.persistence.*
import kotlin.reflect.KClass

@Entity
@Table(name = TABLE_NAME)
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

    @Column(name = TRANSPORT_ID)
    @Convert(converter = TransportConverter::class)
    var transport: Transport? = null

    @Column(name = STATUS_ID)
    @Convert(converter = CourierStatusConverter::class)
    var status: CourierStatus? = null

    companion object {
        const val TABLE_NAME = "dlv_courier"
        const val TRANSPORT_ID = "transport_id"
        const val STATUS_ID = "status_id"

        fun fromDomain(domain: Courier) = CourierJpaEntity().apply {
            id = domain.id
            name = domain.name
            location = domain.location
            transport = domain.transport
            status = domain.status
        }
    }
}