package github.com.hukuta94.delivery.configuration.api.kafka

import github.com.hukuta94.delivery.api.kafka.BasketKafkaConsumer
import github.com.hukuta94.delivery.core.application.usecase.event.SaveIntegrationEventUseCase
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory

@EnableKafka
@Configuration
open class KafkaConsumerConfiguration {

    @Bean
    open fun basketKafkaConsumerFactory(): ConsumerFactory<String, String> {
        val properties = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ConsumerConfig.GROUP_ID_CONFIG to "basket-consumer-group",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        )
        return DefaultKafkaConsumerFactory(properties)
    }

    @Bean
    open fun basketKafkaListenerContainerFactory(
        basketKafkaConsumerFactory: ConsumerFactory<String, String>,
    ): ConcurrentKafkaListenerContainerFactory<String, String> {
        return ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            consumerFactory = basketKafkaConsumerFactory
        }
    }

    @Bean
    open fun basketKafkaConsumer(
        saveIntegrationEventUseCase: SaveIntegrationEventUseCase,
    ) = BasketKafkaConsumer(
        saveIntegrationEventUseCase = saveIntegrationEventUseCase,
    )
}