package github.com.hukuta94.delivery.api.adapter.kafka

import com.google.protobuf.util.JsonFormat
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.UUIDDeserializer
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

abstract class IntegrationEventKafkaConsumer(
    bootstrapServersConfig: String,
    groupIdConfig: String,
    topic: String,
) {
    private val consumer: KafkaConsumer<UUID, String>
    protected val jsonParser = JsonFormat.parser()

    init {
        val props = Properties()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServersConfig
        props[ConsumerConfig.GROUP_ID_CONFIG] = groupIdConfig
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = UUIDDeserializer::class.java.name
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

        consumer = KafkaConsumer<UUID, String>(props).apply {
            this.subscribe(listOf(topic))
        }

        consumeMessages()
    }

    abstract fun handleMessage(message: ConsumerRecord<UUID, String>)

    private fun consumeMessages() {
        consumer.use {
            while (true) {
                val records = consumer.poll(Duration.ofMillis(POLL_DELAY_MS))
                for (record in records) {
                    LOG.info("Begin handling message with key: ${record.key()}")

                    try {
                        handleMessage(record)
                    } catch (ex: java.lang.Exception) {
                        LOG.error(ex.stackTraceToString())
                    }

                    LOG.info("Finish handling message with key: ${record.key()}")
                }
            }
        }
    }

    companion object {
        private const val POLL_DELAY_MS = 100L

        private val LOG = LoggerFactory.getLogger(IntegrationEventKafkaConsumer::class.java)
    }
}