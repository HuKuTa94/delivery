package github.com.hukuta94.delivery.configuration.api

import github.com.hukuta94.delivery.configuration.api.adapter.http.RestControllerConfiguration
import github.com.hukuta94.delivery.configuration.api.adapter.kafka.KafkaConsumerConfiguration
import github.com.hukuta94.delivery.configuration.api.adapter.scheduler.SchedulerConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    RestControllerConfiguration::class,
    KafkaConsumerConfiguration::class,
    SchedulerConfiguration::class,
)
open class ApiConfiguration