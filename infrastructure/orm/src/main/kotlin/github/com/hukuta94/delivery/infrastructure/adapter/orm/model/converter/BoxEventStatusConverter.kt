package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter

import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event.BoxEventStatus
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class BoxEventStatusConverter : AttributeConverter<BoxEventStatus, String> {

    override fun convertToDatabaseColumn(attribute: BoxEventStatus): String {
        return attribute.dbCode
    }

    override fun convertToEntityAttribute(dbData: String): BoxEventStatus {
        return BoxEventStatus.from(dbData)
    }
}