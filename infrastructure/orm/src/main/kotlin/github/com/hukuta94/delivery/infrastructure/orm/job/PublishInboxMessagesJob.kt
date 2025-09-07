package github.com.hukuta94.delivery.infrastructure.orm.job

import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventClassType
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventPublisher
import github.com.hukuta94.delivery.core.domain.IntegrationEvent
import github.com.hukuta94.delivery.infrastructure.orm.model.entity.event.InboxEventJpaEntity
import github.com.hukuta94.delivery.infrastructure.orm.repository.event.InboxEventJpaRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Scheduled

class PublishInboxMessagesJob(
    inboxJpaRepository: InboxEventJpaRepository,
    private val integrationEventPublisher: IntegrationEventPublisher,
    private val integrationEventDeserializer: IntegrationEventDeserializer,
): PublishBoxEventMessageJob<
        IntegrationEvent,
        IntegrationEventClassType,
        InboxEventJpaEntity
>(inboxJpaRepository) {

    override val limitedPageOfMessageCount: PageRequest = LIMITED_COUNT_OF_BOX_MESSAGES

    override fun publishEvent(event: IntegrationEvent) {
        integrationEventPublisher.publish(event)
    }

    override fun convertMessageToEvent(message: InboxEventJpaEntity): IntegrationEvent {
        return message.toEvent(integrationEventDeserializer)
    }

    //TODO Вынести все магические числа, а так же количество выгружаемых outbox сообщений за раз, в файл конфигурации
    @Scheduled(fixedDelay = 5000)
    fun pullMessagesFromInbox() {
        executeJob()
    }

    companion object {
        private const val BOX_MESSAGES_LIMIT = 20

        private val LIMITED_COUNT_OF_BOX_MESSAGES = PageRequest.of(
            0,
            BOX_MESSAGES_LIMIT,
            Sort.by(Sort.Direction.ASC, InboxEventJpaEntity::createdAt.name)
        )
    }
}