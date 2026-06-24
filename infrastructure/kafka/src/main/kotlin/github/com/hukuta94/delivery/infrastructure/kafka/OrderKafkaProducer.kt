package github.com.hukuta94.delivery.infrastructure.kafka

import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedDomainEvent
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedDomainEvent
import github.com.hukuta94.delivery.core.application.port.BusProducerPort
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import java.util.*
import java.util.concurrent.ExecutionException

class OrderKafkaProducer(
    private val kafkaTemplate: KafkaTemplate<UUID, ByteArray>,
) : BusProducerPort {

    override fun publishOrderAssignedDomainEvent(
        orderAssignedDomainEvent: OrderAssignedDomainEvent
    ) {
        publishToTopicOrderStatusChanged(
            orderId = orderAssignedDomainEvent.orderId,
            orderStatus =
                github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus.ASSIGNED.toKafkaOrderStatus()
        )
    }

    override fun publishOrderCompletedDomainEvent(
        orderCompletedDomainEvent: OrderCompletedDomainEvent
    ) {
        publishToTopicOrderStatusChanged(
            orderId = orderCompletedDomainEvent.orderId,
            orderStatus =
                github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus.COMPLETED.toKafkaOrderStatus()
        )
    }

    private fun publishToTopicOrderStatusChanged(orderId: UUID, orderStatus: OrderStatus) {
        val integrationEvent = OrderStatusChangedIntegrationEvent.newBuilder()
            .setOrderId(orderId.toString())
            .setOrderStatus(orderStatus)
            .build()

        val message = ProducerRecord(
            TOPIC_ORDER_STATUS_CHANGED,
            orderId,
            integrationEvent.toByteArray() // TODO Отправлять в формате JSON
        )

        try {
            kafkaTemplate.send(message).get()
        } catch (ex: ExecutionException) {
            throw IllegalStateException(
                "Broker did not acknowledge message with key: $orderId: ${ex.cause?.message}",
                ex,
            )
        } catch (ex: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IllegalStateException(
                "Interrupted while waiting ack for key: $orderId",
                ex,
            )
        }

        LOG.info(
            "Integration event OrderStatusChanged to \"${orderStatus.name}\" " +
            "with key: $orderId was acknowledged by topic: $TOPIC_ORDER_STATUS_CHANGED"
        )
    }

    private fun github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus.toKafkaOrderStatus(): OrderStatus {
        return when (this) {
            github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus.CREATED -> OrderStatus.Created
            github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus.ASSIGNED -> OrderStatus.Assigned
            github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus.COMPLETED -> OrderStatus.Completed
        }
    }

    companion object {
        private const val TOPIC_ORDER_STATUS_CHANGED = "order.status.changed"

        private val LOG = LoggerFactory.getLogger(OrderKafkaProducer::class.java)
    }
}
