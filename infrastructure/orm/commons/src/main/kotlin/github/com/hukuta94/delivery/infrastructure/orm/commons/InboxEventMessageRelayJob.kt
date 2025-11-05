package github.com.hukuta94.delivery.infrastructure.orm.commons

import github.com.hukuta94.delivery.core.application.event.ApplicationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.ApplicationEventPublisher
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageRelay
import github.com.hukuta94.delivery.core.application.port.repository.event.BoxEventMessageRelayRepositoryPort
import org.springframework.scheduling.annotation.Scheduled

class InboxEventMessageRelayJob(
    eventRepository: BoxEventMessageRelayRepositoryPort,
    eventDeserializer: ApplicationEventDeserializer,
    eventPublisher: ApplicationEventPublisher,
): BoxEventMessageRelay(
    eventRepository,
    eventDeserializer,
    eventPublisher,
) {
    //TODO Вынести все магические числа, а так же количество выгружаемых outbox сообщений за раз, в файл конфигурации
    @Scheduled(fixedDelay = 5000)
    fun pullMessagesFromInbox() {
        execute()
    }
}