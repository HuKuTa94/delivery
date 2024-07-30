package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter

import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event.OutboxEventStatus
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class OutboxEventStatusConverter : AttributeConverter<OutboxEventStatus, String> {

    override fun convertToDatabaseColumn(OutboxEventStatus: OutboxEventStatus): String {
        return OutboxEventStatus.dbCode
    }

    override fun convertToEntityAttribute(dbData: String): OutboxEventStatus {
        return OutboxEventStatus.from(dbData)
    }
}