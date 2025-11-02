package github.com.hukuta94.delivery.infrastructure.orm.model.converter

import github.com.hukuta94.delivery.infrastructure.orm.model.entity.event.BoxEventStatus
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class BoxEventStatusConverter : AttributeConverter<BoxEventStatus, Int> {

    override fun convertToDatabaseColumn(attribute: BoxEventStatus): Int {
        return attribute.id
    }

    override fun convertToEntityAttribute(dbData: Int): BoxEventStatus {
        return BoxEventStatus.from(dbData)
    }
}