package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter

import github.com.hukuta94.delivery.core.domain.order.OrderStatus
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class OrderStatusConverter : AttributeConverter<OrderStatus, String> {

    override fun convertToDatabaseColumn(attribute: OrderStatus): String {
        return attribute.name
    }

    override fun convertToEntityAttribute(dbData: String): OrderStatus {
        return OrderStatus.valueOf(dbData)
    }
}