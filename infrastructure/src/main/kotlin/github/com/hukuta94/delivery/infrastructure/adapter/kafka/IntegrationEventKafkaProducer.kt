package github.com.hukuta94.delivery.infrastructure.adapter.kafka

import github.com.hukuta94.delivery.core.port.BusProducer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.UUIDSerializer
import org.slf4j.LoggerFactory
import java.util.*

abstract class IntegrationEventKafkaProducer(
    bootstrapServersConfig: String,
): BusProducer {
    private val producer: KafkaProducer<UUID, ByteArray>

    init {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServersConfig
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = UUIDSerializer::class.java.name
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = ByteArraySerializer::class.java.name

        producer = KafkaProducer<UUID, ByteArray>(props)
    }

    protected fun sendMessage(
        topic: String,
        key: UUID,
        value: ByteArray
    ) {
        producer.use {
            val record = ProducerRecord(topic, key, value)
            producer.send(record) { metadata, exception ->
                if (exception != null) {
                    LOG.error("Error sending message with key $key: ${exception.message}")
                } else {
                    LOG.info("Message with key $key sent to partition ${metadata.partition()} with offset ${metadata.offset()}")
                }
            }
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(IntegrationEventKafkaProducer::class.java)
    }
}