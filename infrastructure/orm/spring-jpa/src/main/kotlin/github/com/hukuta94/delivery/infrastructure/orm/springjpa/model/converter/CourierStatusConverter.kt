package github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.converter

import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class CourierStatusConverter : AttributeConverter<CourierStatus, Int> {

    override fun convertToDatabaseColumn(courierStatus: CourierStatus): Int {
        return courierStatus.id
    }

    override fun convertToEntityAttribute(courierStatusId: Int): CourierStatus {
        return CourierStatus.from(courierStatusId)
    }
}