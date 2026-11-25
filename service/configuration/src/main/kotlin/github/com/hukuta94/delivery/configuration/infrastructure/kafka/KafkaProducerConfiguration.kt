package github.com.hukuta94.delivery.configuration.infrastructure.kafka

import github.com.hukuta94.delivery.core.application.port.BusProducerPort
import github.com.hukuta94.delivery.infrastructure.kafka.OrderKafkaProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.KafkaTemplate
import java.util.*

@EnableKafka
@Configuration
open class KafkaProducerConfiguration(
    private val kafkaTemplate: KafkaTemplate<UUID, ByteArray>
) {

    @Bean
    open fun orderKafkaProducer(): BusProducerPort =
        OrderKafkaProducer(
            kafkaTemplate = kafkaTemplate,
        )
}
