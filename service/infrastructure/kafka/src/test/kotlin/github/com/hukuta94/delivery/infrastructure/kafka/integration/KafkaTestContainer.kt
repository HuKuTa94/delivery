package github.com.hukuta94.delivery.infrastructure.kafka.integration

import org.testcontainers.kafka.KafkaContainer
import org.testcontainers.utility.DockerImageName

/**
 * Singleton Apache Kafka (KRaft) container shared across integration specs in the test JVM.
 * Uses the locally available image; requires Docker.
 */
object KafkaTestContainer {

    val bootstrapServers: String get() = container.bootstrapServers

    private val container: KafkaContainer = KafkaContainer(DockerImageName.parse(IMAGE))
        .apply {
            start()
            // Ryuk is disabled (see root build), so stop the container explicitly on JVM exit.
            Runtime.getRuntime().addShutdownHook(Thread { stop() })
        }

    private const val IMAGE = "apache/kafka:3.8.0"
}
