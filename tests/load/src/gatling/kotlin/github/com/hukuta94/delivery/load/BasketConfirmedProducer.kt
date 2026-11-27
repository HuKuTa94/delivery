package github.com.hukuta94.delivery.load

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * Sends `basket.confirmed` events of new baskets, keyed by the basket id, without waiting for the broker,
 * and counts how the broker answered them.
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
    private val acknowledged = AtomicInteger()
    private val failed = AtomicInteger()
    private val firstFailure = AtomicReference<Exception?>()

    // Fails before the load when the broker or the topic is missing, and fetches the metadata the first sends
    // would wait for
    init {
        producer.partitionsFor(TOPIC)
    }

    fun send() {
        val basketId = UUID.randomUUID().toString()
        producer.send(ProducerRecord(TOPIC, basketId, event(basketId))) { _, exception ->
            if (exception == null) {
                acknowledged.incrementAndGet()
            } else {
                failed.incrementAndGet()
                firstFailure.compareAndSet(null, exception)
            }
        }
    }

    /**
     * Waits until the broker has answered every event sent so far.
     */
    fun flush(): SentEvents {
        producer.flush()
        return SentEvents(
            acknowledged = acknowledged.get(),
            failed = failed.get(),
            firstFailure = firstFailure.get(),
        )
    }

    override fun close() {
        producer.close()
    }

    // Protobuf JSON of the BasketConfirmedIntegrationEvent contract: its fields are PascalCase, so are their JSON names
    private fun event(basketId: String) = """{"BasketId":"$basketId","Address":{"Street":"$STREET"}}"""

    private companion object {
        const val TOPIC = "basket.confirmed"
        const val STREET = "Load test street"
        const val MAX_BLOCK_MS = 10_000
    }
}

/**
 * Events sent, counted by the answer of the broker; [firstFailure] tells why the first rejected event failed.
 */
data class SentEvents(
    val acknowledged: Int,
    val failed: Int,
    val firstFailure: Exception?,
) {
    override fun toString() = "$acknowledged events acknowledged by the broker, $failed failed" +
        (firstFailure?.let { " (first: $it)" } ?: "")
}
