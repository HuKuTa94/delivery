package github.com.hukuta94.delivery.configuration.infrastructure

import github.com.hukuta94.delivery.configuration.infrastructure.kafka.KafkaProducerConfiguration
import github.com.hukuta94.delivery.configuration.infrastructure.orm.ktorm.KtormOrmConfiguration
import github.com.hukuta94.delivery.configuration.infrastructure.orm.springjpa.SpringOrmConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    //TODO Нужно как-то разбить конфигурацию на артефакты, чтобы удобно было выбирать нужную реализацию
    KtormOrmConfiguration::class,
    SpringOrmConfiguration::class,
    KafkaProducerConfiguration::class,
)
open class InfrastructureConfiguration