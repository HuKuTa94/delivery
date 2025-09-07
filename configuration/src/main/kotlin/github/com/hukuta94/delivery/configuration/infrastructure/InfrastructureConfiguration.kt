package github.com.hukuta94.delivery.configuration.infrastructure

import github.com.hukuta94.delivery.configuration.infrastructure.kafka.KafkaProducerConfiguration
import github.com.hukuta94.delivery.configuration.infrastructure.orm.OrmRepositoryConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    OrmRepositoryConfiguration::class,
    KafkaProducerConfiguration::class,
)
open class InfrastructureConfiguration