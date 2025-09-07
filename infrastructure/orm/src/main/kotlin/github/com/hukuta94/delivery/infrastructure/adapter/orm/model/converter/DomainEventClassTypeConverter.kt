package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventClassType
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class DomainEventClassTypeConverter : AttributeConverter<DomainEventClassType, String> {

    override fun convertToDatabaseColumn(attribute: DomainEventClassType): String {
        return attribute.stringValue()
    }

    override fun convertToEntityAttribute(dbData: String): DomainEventClassType {
        return DomainEventClassType.from(dbData)
    }
}