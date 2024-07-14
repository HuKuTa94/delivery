package github.com.hukuta94.delivery.infrastructure.adapter.kafka

import github.com.hukuta94.delivery.core.domain.order.OrderAssignedDomainEvent
import github.com.hukuta94.delivery.core.domain.order.OrderCompletedDomainEvent
import java.util.*

class OrderIntegrationEventKafkaProducer(
    bootstrapServersConfig: String,
) : IntegrationEventKafkaProducer(bootstrapServersConfig) {

    override fun publishOrderAssignedDomainEvent(
        orderAssignedDomainEvent: OrderAssignedDomainEvent
    ) {
        publishToTopicOrderStatusChanged(
            orderId = orderAssignedDomainEvent.id,
            orderStatus = github.com.hukuta94.delivery.core.domain.order.OrderStatus.ASSIGNED.toKafkaOrderStatus()
        )
    }

    override fun publishOrderCompletedDomainEvent(
        orderCompletedDomainEvent: OrderCompletedDomainEvent
    ) {
        publishToTopicOrderStatusChanged(
            orderId = orderCompletedDomainEvent.id,
            orderStatus = github.com.hukuta94.delivery.core.domain.order.OrderStatus.COMPLETED.toKafkaOrderStatus()
        )
    }

    private fun publishToTopicOrderStatusChanged(orderId: UUID, orderStatus: OrderStatus) {
        val integrationEvent = OrderStatusChangedIntegrationEvent.newBuilder()
            .setOrderId(orderId.toString())
            .setOrderStatus(orderStatus)
            .build()

        sendMessage(
            topic = TOPIC_ORDER_STATUS_CHANGED,
            key = orderId,
            value = integrationEvent.toByteArray(), //TODO Отправлять в формате JSON
        )
    }

    private fun github.com.hukuta94.delivery.core.domain.order.OrderStatus.toKafkaOrderStatus(): OrderStatus {
        return when (this) {
            github.com.hukuta94.delivery.core.domain.order.OrderStatus.CREATED -> OrderStatus.Created
            github.com.hukuta94.delivery.core.domain.order.OrderStatus.ASSIGNED -> OrderStatus.Assigned
            github.com.hukuta94.delivery.core.domain.order.OrderStatus.COMPLETED -> OrderStatus.Completed
        }
    }

    companion object {
        private const val TOPIC_ORDER_STATUS_CHANGED = "order.status.changed"
    }
}