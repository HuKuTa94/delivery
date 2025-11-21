package github.com.hukuta94.delivery.infrastructure.orm.spring.model.converter

import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class BoxEventMessageStatusConverter : AttributeConverter<BoxEventMessageStatus, Int> {

    override fun convertToDatabaseColumn(attribute: BoxEventMessageStatus): Int {
        return attribute.id
    }

    override fun convertToEntityAttribute(dbData: Int): BoxEventMessageStatus {
        return BoxEventMessageStatus.from(dbData)
    }
}
