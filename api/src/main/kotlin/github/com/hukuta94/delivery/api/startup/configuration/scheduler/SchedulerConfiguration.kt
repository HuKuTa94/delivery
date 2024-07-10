package github.com.hukuta94.delivery.api.startup.configuration.scheduler

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@Import(
    OrderSchedulerConfiguration::class,
    CourierSchedulerConfiguration::class,
)
open class SchedulerConfiguration