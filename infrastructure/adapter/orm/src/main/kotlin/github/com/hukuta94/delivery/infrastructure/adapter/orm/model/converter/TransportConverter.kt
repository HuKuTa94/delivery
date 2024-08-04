package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter

import github.com.hukuta94.delivery.core.domain.aggregate.courier.Transport
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class TransportConverter : AttributeConverter<Transport, Int> {

    override fun convertToDatabaseColumn(transport: Transport): Int {
        return transport.id
    }

    override fun convertToEntityAttribute(transportId: Int): Transport {
        return Transport.from(transportId)
    }
}