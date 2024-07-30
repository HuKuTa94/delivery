package github.com.hukuta94.delivery.startup.configuration.infrastructure

import github.com.hukuta94.delivery.startup.configuration.infrastructure.adapter.kafka.KafkaProducerConfiguration
import github.com.hukuta94.delivery.startup.configuration.infrastructure.adapter.orm.OrmRepositoryConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    OrmRepositoryConfiguration::class,
    KafkaProducerConfiguration::class,
)
open class InfrastructureConfiguration