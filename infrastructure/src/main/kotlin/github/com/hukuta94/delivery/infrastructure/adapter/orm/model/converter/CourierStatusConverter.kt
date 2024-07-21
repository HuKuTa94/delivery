package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter

import github.com.hukuta94.delivery.core.domain.courier.CourierStatus
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class CourierStatusConverter : AttributeConverter<CourierStatus, String> {

    override fun convertToDatabaseColumn(attribute: CourierStatus): String {
        return attribute.name
    }

    override fun convertToEntityAttribute(dbData: String): CourierStatus {
        return CourierStatus.valueOf(dbData)
    }
}