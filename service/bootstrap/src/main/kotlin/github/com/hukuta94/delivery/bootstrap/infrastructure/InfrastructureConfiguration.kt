package github.com.hukuta94.delivery.bootstrap.infrastructure

import github.com.hukuta94.delivery.bootstrap.infrastructure.kafka.KafkaProducerConfiguration
import github.com.hukuta94.delivery.bootstrap.infrastructure.orm.ktorm.KtormOrmConfiguration
import github.com.hukuta94.delivery.bootstrap.infrastructure.orm.springjpa.SpringOrmConfiguration
import github.com.hukuta94.delivery.bootstrap.infrastructure.persistence.inmemory.InMemoryRepositoryConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@EnableConfigurationProperties(RelayProperties::class)
@Import(
    SpringOrmConfiguration::class,
    KtormOrmConfiguration::class,
    InMemoryRepositoryConfiguration::class,
    KafkaProducerConfiguration::class,
)
open class InfrastructureConfiguration
