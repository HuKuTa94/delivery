package github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.converter

import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class OrderStatusConverter : AttributeConverter<OrderStatus, Int> {

    override fun convertToDatabaseColumn(orderStatus: OrderStatus): Int {
        return orderStatus.id
    }

    override fun convertToEntityAttribute(orderStatusId: Int): OrderStatus {
        return OrderStatus.from(orderStatusId)
    }
}