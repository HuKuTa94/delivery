package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter

import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventClassType
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class IntegrationEventClassTypeConverter : AttributeConverter<IntegrationEventClassType, String> {

    override fun convertToDatabaseColumn(attribute: IntegrationEventClassType): String {
        return attribute.stringValue()
    }

    override fun convertToEntityAttribute(dbData: String): IntegrationEventClassType {
        return IntegrationEventClassType.from(dbData)
    }
}