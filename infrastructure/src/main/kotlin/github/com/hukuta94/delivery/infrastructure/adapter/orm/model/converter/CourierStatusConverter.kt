package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter

import github.com.hukuta94.delivery.core.domain.courier.CourierStatus
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class CourierStatusConverter : AttributeConverter<CourierStatus, Int> {

    override fun convertToDatabaseColumn(courierStatus: CourierStatus): Int {
        return courierStatus.id
    }

    override fun convertToEntityAttribute(courierStatusId: Int): CourierStatus {
        return CourierStatus.from(courierStatusId)
    }
}