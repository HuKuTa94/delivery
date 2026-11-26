package github.com.hukuta94.delivery.bootstrap.api.scheduler

import github.com.hukuta94.delivery.api.scheduler.courier.CourierScheduler
import github.com.hukuta94.delivery.api.scheduler.order.OrderScheduler
import github.com.hukuta94.delivery.infrastructure.orm.commons.InboxEventMessageRelayJob
import github.com.hukuta94.delivery.infrastructure.orm.commons.OutboxEventMessageRelayJob
import org.springframework.boot.task.ThreadPoolTaskSchedulerBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

/**
 * Every scheduled job runs on a scheduler of its own with one thread, picked by `@Scheduled(scheduler = ...)`.
 * Spring Boot's default is one scheduler with one thread for all jobs, so a slow job delayed all the others.
 *
 * One thread per job: a job never overlaps itself. The jobs do run at the same time as each other now, which is safe
 * while they change different rows: assignment takes free couriers and created orders, movement takes busy couriers
 * and assigned orders, the relays take their own inbox/outbox messages (FOR UPDATE SKIP LOCKED).
 *
 * The beans replace Spring Boot's default `taskScheduler`; the builder keeps `spring.task.scheduling.*` settings.
 */
@Configuration
@EnableScheduling
@Import(
    OrderSchedulerConfiguration::class,
    CourierSchedulerConfiguration::class,
)
open class SchedulerConfiguration {

    @Bean(OrderScheduler.TASK_SCHEDULER)
    open fun orderAssignmentTaskScheduler(builder: ThreadPoolTaskSchedulerBuilder): ThreadPoolTaskScheduler =
        builder.singleThread("order-assignment-")

    @Bean(CourierScheduler.TASK_SCHEDULER)
    open fun courierMovementTaskScheduler(builder: ThreadPoolTaskSchedulerBuilder): ThreadPoolTaskScheduler =
        builder.singleThread("courier-movement-")

    @Bean(InboxEventMessageRelayJob.TASK_SCHEDULER)
    open fun inboxRelayTaskScheduler(builder: ThreadPoolTaskSchedulerBuilder): ThreadPoolTaskScheduler =
        builder.singleThread("inbox-relay-")

    @Bean(OutboxEventMessageRelayJob.TASK_SCHEDULER)
    open fun outboxRelayTaskScheduler(builder: ThreadPoolTaskSchedulerBuilder): ThreadPoolTaskScheduler =
        builder.singleThread("outbox-relay-")

    private fun ThreadPoolTaskSchedulerBuilder.singleThread(threadNamePrefix: String): ThreadPoolTaskScheduler = this
        .poolSize(1)
        .threadNamePrefix(threadNamePrefix)
        .build()
}
