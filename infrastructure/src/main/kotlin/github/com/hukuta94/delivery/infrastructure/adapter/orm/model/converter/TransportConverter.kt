package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter

import github.com.hukuta94.delivery.core.domain.courier.Transport
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class TransportConverter : AttributeConverter<Transport, String> {

    override fun convertToDatabaseColumn(attribute: Transport): String {
        return attribute.name
    }

    override fun convertToEntityAttribute(dbData: String): Transport {
        return Transport.valueOf(dbData)
    }
}