package github.com.hukuta94.delivery.e2e

import com.google.protobuf.util.JsonFormat
import github.com.hukuta94.delivery.api.kafka.address
import github.com.hukuta94.delivery.api.kafka.basketConfirmedIntegrationEvent
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.UUID

/**
 * Sends `basket.confirmed` events as protobuf JSON of the `BasketConfirmedIntegrationEvent` contract,
 * keyed by the basket id.
 */
class BasketConfirmedProducer(bootstrapServers: String) : AutoCloseable {
    private val producer = KafkaProducer(
        mapOf<String, Any>(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
            // Fail fast when the broker is not running instead of the default minute
            ProducerConfig.MAX_BLOCK_MS_CONFIG to MAX_BLOCK_MS,
        ),
        StringSerializer(),
        StringSerializer(),
    )

    /**
     * Returns once the broker has acknowledged the event.
     */
    fun send(basketId: UUID) {
        val event = basketConfirmedIntegrationEvent {
            this.basketId = basketId.toString()
            address = address {
                street = STREET
            }
        }
        val record = ProducerRecord(
            TOPIC,
            basketId.toString(),
            JSON_PRINTER.print(event),
        )
        producer.send(record).get()
    }

    override fun close() {
        producer.close()
    }

    companion object {
        private const val TOPIC = "basket.confirmed"
        private const val STREET = "End-to-end street"
        private const val MAX_BLOCK_MS = 10_000
        private val JSON_PRINTER = JsonFormat.printer()
    }
}
