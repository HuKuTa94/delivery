package github.com.hukuta94.delivery.infrastructure.orm.job

import github.com.hukuta94.delivery.core.application.event.ApplicationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.ApplicationEventPublisher
import github.com.hukuta94.delivery.infrastructure.orm.model.entity.event.OutboxEventJpaEntity
import github.com.hukuta94.delivery.infrastructure.orm.repository.event.OutboxEventJpaRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Scheduled

class PublishOutboxMessagesJob(
    eventJpaRepository: OutboxEventJpaRepository,
    eventDeserializer: ApplicationEventDeserializer,
    eventPublisher: ApplicationEventPublisher,
): PublishBoxEventMessageJob<OutboxEventJpaEntity>(
    eventJpaRepository,
    eventDeserializer,
    eventPublisher,
) {
    //TODO Вынести все магические числа, а так же количество выгружаемых outbox сообщений за раз, в файл конфигурации
    @Scheduled(fixedDelay = 5000)
    fun pullMessagesFromOutbox() {
        executeJob(LIMITED_COUNT_OF_BOX_MESSAGES)
    }

    companion object {
        private const val BOX_MESSAGES_LIMIT = 20

        private val LIMITED_COUNT_OF_BOX_MESSAGES = PageRequest.of(
            0,
            BOX_MESSAGES_LIMIT,
            Sort.by(Sort.Direction.ASC, OutboxEventJpaEntity::createdAt.name)
        )
    }
}