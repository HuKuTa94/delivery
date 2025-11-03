package github.com.hukuta94.delivery.configuration.infrastructure

import github.com.hukuta94.delivery.configuration.infrastructure.kafka.KafkaProducerConfiguration
import github.com.hukuta94.delivery.configuration.infrastructure.orm.SpringJpaRepositoryConfiguration
import github.com.hukuta94.delivery.configuration.infrastructure.orm.ktorm.KtormOrmConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    //TODO Нужно как-то разбить конфигурацию на артефакты, чтобы удобно было выбирать нужную реализацию
    KtormOrmConfiguration::class,
    SpringJpaRepositoryConfiguration::class,
    KafkaProducerConfiguration::class,
)
open class InfrastructureConfiguration