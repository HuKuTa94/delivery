package github.com.hukuta94.delivery.infrastructure.kafka

import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedDomainEvent
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedDomainEvent
import github.com.hukuta94.delivery.infrastructure.kafka.integration.KafkaTestContainer
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.UUIDDeserializer
import org.apache.kafka.common.serialization.UUIDSerializer
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.utils.KafkaTestUtils
import java.time.Duration
import java.util.UUID

internal class OrderKafkaProducerIT : StringSpec({

    val topic = "order.status.changed"
    val producerProperties = mapOf<String, Any>(
        ProducerConfig.ACKS_CONFIG to "all",
        ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to true,
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to KafkaTestContainer.bootstrapServers,
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to UUIDSerializer::class.java,
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to ByteArraySerializer::class.java,
    )
    val kafkaTemplate = KafkaTemplate(DefaultKafkaProducerFactory<UUID, ByteArray>(producerProperties))
    val sut = OrderKafkaProducer(kafkaTemplate)

    fun assertPublishedWithStatus(orderId: UUID, expectedStatus: OrderStatus) {
        val record = consumeRecordWithKey(topic, orderId)
        assertSoftly {
            record.key() shouldBe orderId
            val payload = OrderStatusChangedIntegrationEvent.parseFrom(record.value())
            payload.orderId shouldBe orderId.toString()
            payload.orderStatus shouldBe expectedStatus
        }
    }

    "publishes OrderAssigned to the broker with the orderId as key and Assigned status" {
        val orderId = UUID.randomUUID()

        sut.publishOrderAssignedDomainEvent(OrderAssignedDomainEvent(orderId = orderId, courierId = UUID.randomUUID()))

        assertPublishedWithStatus(orderId, OrderStatus.Assigned)
    }

    "publishes OrderCompleted to the broker with the orderId as key and Completed status" {
        val orderId = UUID.randomUUID()

        sut.publishOrderCompletedDomainEvent(OrderCompletedDomainEvent(orderId = orderId))

        assertPublishedWithStatus(orderId, OrderStatus.Completed)
    }
})

private const val POLL_ATTEMPTS = 30
private val POLL_INTERVAL = Duration.ofMillis(500)

/**
 * Polls a fresh consumer (own group, read from earliest) until a record with [key] appears on [topic],
 * since broker delivery is asynchronous and the topic is shared between tests.
 */
private fun consumeRecordWithKey(topic: String, key: UUID): ConsumerRecord<UUID, ByteArray> {
    val consumerProperties = KafkaTestUtils.consumerProps(
        KafkaTestContainer.bootstrapServers,
        "it-${UUID.randomUUID()}",
        "false",
    )
    val consumerFactory = DefaultKafkaConsumerFactory(consumerProperties, UUIDDeserializer(), ByteArrayDeserializer())
    return consumerFactory.createConsumer().use { consumer ->
        consumer.subscribe(listOf(topic))
        generateSequence { consumer.poll(POLL_INTERVAL) }
            .take(POLL_ATTEMPTS)
            .flatMap { it.records(topic).asSequence() }
            .firstOrNull { it.key() == key }
            ?: error("No record with key=$key received from topic '$topic' within the timeout")
    }
}
