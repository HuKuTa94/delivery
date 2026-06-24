package github.com.hukuta94.delivery.configuration.infrastructure

import github.com.hukuta94.delivery.configuration.infrastructure.kafka.KafkaProducerConfiguration
import github.com.hukuta94.delivery.configuration.infrastructure.orm.ktorm.KtormOrmConfiguration
import github.com.hukuta94.delivery.configuration.infrastructure.orm.springjpa.SpringOrmConfiguration
import github.com.hukuta94.delivery.configuration.infrastructure.persistence.inmemory.InMemoryRepositoryConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    SpringOrmConfiguration::class,
    KtormOrmConfiguration::class,
    InMemoryRepositoryConfiguration::class,
    KafkaProducerConfiguration::class,
)
open class InfrastructureConfiguration
