package github.com.hukuta94.delivery.api.startup.configuration.adapter.kafka

import github.com.hukuta94.delivery.api.adapter.kafka.BasketKafkaConsumer
import github.com.hukuta94.delivery.core.port.repository.box.InboxRepository
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.UUIDDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import java.util.*

@Configuration
open class KafkaConsumerConfiguration {

    @Bean
    open fun basketKafkaConsumerFactory(): ConsumerFactory<UUID, String> {
        val properties = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ConsumerConfig.GROUP_ID_CONFIG to "basket-consumer-group",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to UUIDDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        )
        return DefaultKafkaConsumerFactory(properties)
    }

    @Bean
    open fun basketKafkaListenerContainerFactory(
        basketKafkaConsumerFactory: ConsumerFactory<UUID, String>,
    ): ConcurrentKafkaListenerContainerFactory<UUID, String> {
        return ConcurrentKafkaListenerContainerFactory<UUID, String>().apply {
            consumerFactory = basketKafkaConsumerFactory
        }
    }

    @Bean
    open fun basketKafkaConsumer(
        inboxRepository: InboxRepository,
    ) = BasketKafkaConsumer(
        inboxRepository = inboxRepository,
    )
}