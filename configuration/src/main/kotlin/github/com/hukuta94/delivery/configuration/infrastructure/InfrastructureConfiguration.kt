package github.com.hukuta94.delivery.configuration.infrastructure

import github.com.hukuta94.delivery.configuration.infrastructure.kafka.KafkaProducerConfiguration
import github.com.hukuta94.delivery.configuration.infrastructure.orm.SpringJpaRepositoryConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    SpringJpaRepositoryConfiguration::class,
    KafkaProducerConfiguration::class,
)
open class InfrastructureConfiguration