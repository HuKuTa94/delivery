package github.com.hukuta94.delivery.infrastructure.orm.model.converter

import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventClassType
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class IntegrationEventClassTypeConverter : AttributeConverter<IntegrationEventClassType, String> {

    override fun convertToDatabaseColumn(attribute: IntegrationEventClassType): String {
        return attribute.stringValue()
    }

    override fun convertToEntityAttribute(dbData: String): IntegrationEventClassType {
        return IntegrationEventClassType.from(dbData)
    }
}