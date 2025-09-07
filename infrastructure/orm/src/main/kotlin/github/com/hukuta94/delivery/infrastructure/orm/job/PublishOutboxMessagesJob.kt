package github.com.hukuta94.delivery.infrastructure.orm.job

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventClassType
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventDeserializer
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventPublisher
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.model.entity.event.OutboxEventJpaEntity
import github.com.hukuta94.delivery.infrastructure.orm.repository.event.OutboxEventJpaRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Scheduled

class PublishOutboxMessagesJob(
    outboxEventJpaRepository: OutboxEventJpaRepository,
    private val domainEventPublisher: DomainEventPublisher,
    private val domainEventDeserializer: DomainEventDeserializer,
): PublishBoxEventMessageJob<
        DomainEvent,
        DomainEventClassType,
        OutboxEventJpaEntity
>(outboxEventJpaRepository) {

    override val limitedPageOfMessageCount: PageRequest = LIMITED_COUNT_OF_BOX_MESSAGES

    override fun publishEvent(event: DomainEvent) {
        domainEventPublisher.publish(event)
    }

    override fun convertMessageToEvent(message: OutboxEventJpaEntity): DomainEvent {
        return message.toEvent(domainEventDeserializer)
    }

    //TODO Вынести все магические числа, а так же количество выгружаемых outbox сообщений за раз, в файл конфигурации
    @Scheduled(fixedDelay = 5000)
    fun pullMessagesFromOutbox() {
        executeJob()
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