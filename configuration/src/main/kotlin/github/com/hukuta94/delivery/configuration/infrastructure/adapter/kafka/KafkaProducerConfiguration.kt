package github.com.hukuta94.delivery.configuration.infrastructure.adapter.kafka

import github.com.hukuta94.delivery.core.port.BusProducer
import github.com.hukuta94.delivery.infrastructure.adapter.kafka.OrderKafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.UUIDSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import java.util.*

@EnableKafka
@Configuration
open class KafkaProducerConfiguration {

    //TODO Вынести все магические числа в файл конфигурации
    @Bean
    open fun orderKafkaProducerFactory(): ProducerFactory<UUID, ByteArray> {
        val properties = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to UUIDSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to ByteArraySerializer::class.java,
            ProducerConfig.MAX_BLOCK_MS_CONFIG to 5000,
        )
        return DefaultKafkaProducerFactory(properties)
    }

    @Bean
    open fun orderKafkaTemplate(
        orderKafkaProducerFactory: ProducerFactory<UUID, ByteArray>
    ) = KafkaTemplate(orderKafkaProducerFactory)

    @Bean
    open fun orderKafkaProducer(
        orderKafkaTemplate: KafkaTemplate<UUID, ByteArray>,
    ): BusProducer = OrderKafkaProducer(
        kafkaTemplate = orderKafkaTemplate,
    )
}