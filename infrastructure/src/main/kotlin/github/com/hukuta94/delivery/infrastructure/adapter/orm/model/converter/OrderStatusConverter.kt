package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter

import github.com.hukuta94.delivery.core.domain.order.OrderStatus
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class OrderStatusConverter : AttributeConverter<OrderStatus, Int> {

    override fun convertToDatabaseColumn(orderStatus: OrderStatus): Int {
        return orderStatus.id
    }

    override fun convertToEntityAttribute(orderStatusId: Int): OrderStatus {
        return OrderStatus.from(orderStatusId)
    }
}