package github.com.hukuta94.delivery.infrastructure.orm.spring.model.converter

import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierName
import github.com.hukuta94.delivery.infrastructure.orm.commons.toCourierName
import github.com.hukuta94.delivery.infrastructure.orm.commons.toDb
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class CourierNameConverter : AttributeConverter<CourierName, String> {

    override fun convertToDatabaseColumn(attribute: CourierName): String {
        return attribute.toDb()
    }

    override fun convertToEntityAttribute(dbData: String): CourierName {
        return dbData.toCourierName()
    }
}