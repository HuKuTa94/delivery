package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter

import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event.BoxEventStatus
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class BoxEventStatusConverter : AttributeConverter<BoxEventStatus, String> {

    override fun convertToDatabaseColumn(BoxEventStatus: BoxEventStatus): String {
        return BoxEventStatus.dbCode
    }

    override fun convertToEntityAttribute(dbData: String): BoxEventStatus {
        return BoxEventStatus.from(dbData)
    }
}