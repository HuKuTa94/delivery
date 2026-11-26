package github.com.hukuta94.delivery.e2e

import github.com.hukuta94.delivery.infrastructure.kafka.OrderStatus
import github.com.hukuta94.delivery.infrastructure.kafka.OrderStatusChangedIntegrationEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import java.time.Duration
import java.util.UUID

/**
 * Reads `order.status.changed` from the moment it is created: records published before are skipped.
 * It reads without a consumer group, so it takes no partitions and commits no offsets of other readers of the topic.
 */
class OrderStatusChangedConsumer(bootstrapServers: String) : AutoCloseable {
    private val consumer = KafkaConsumer(
        mapOf<String, Any>(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            // Fail fast when the broker is not running instead of the default minute
            ConsumerConfig.DEFAULT_API_TIMEOUT_MS_CONFIG to API_TIMEOUT_MS,
        ),
        ByteArrayDeserializer(),
        ByteArrayDeserializer(),
    )
    private val statusesByOrder = mutableMapOf<UUID, MutableList<OrderStatus>>()

    init {
        val partitions = consumer
            .partitionsFor(TOPIC)
            .map { TopicPartition(it.topic(), it.partition()) }
        consumer.assign(partitions)
        consumer.seekToEnd(partitions)
        // seekToEnd is lazy: resolve the end offsets now, before the test sends anything
        partitions.forEach { consumer.position(it) }
    }

    /**
     * Polls the topic once, then returns the statuses of the order received so far, in the order they were published.
     */
    fun statusesOf(orderId: UUID): List<OrderStatus> {
        consumer.poll(POLL_TIMEOUT).forEach { record ->
            val event = OrderStatusChangedIntegrationEvent.parseFrom(record.value())
            statusesByOrder
                .getOrPut(UUID.fromString(event.orderId)) { mutableListOf() }
                .add(event.orderStatus)
        }
        return statusesByOrder[orderId].orEmpty()
    }

    override fun close() {
        consumer.close()
    }

    companion object {
        private const val TOPIC = "order.status.changed"
        private const val API_TIMEOUT_MS = 10_000
        private val POLL_TIMEOUT = Duration.ofMillis(500)
    }
}
