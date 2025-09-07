package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter

import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierName
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class CourierNameConverter : AttributeConverter<CourierName, String> {

    override fun convertToDatabaseColumn(attribute: CourierName): String {
        return attribute.value
    }

    override fun convertToEntityAttribute(dbData: String): CourierName {
        return CourierName(dbData)
    }
}