package github.com.hukuta94.delivery.api.startup.configuration.adapter

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.kafka.annotation.EnableKafka

@Configuration
@EnableKafka
@Import(
    KafkaConsumerConfiguration::class,
    KafkaProducerConfiguration::class,
)
open class KafkaConfiguration