package github.com.hukuta94.delivery.infrastructure.orm.commons

import github.com.hukuta94.delivery.core.application.event.ApplicationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.ApplicationEventPublisher
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageRelay
import github.com.hukuta94.delivery.core.application.port.repository.event.BoxEventMessageRelayRepositoryPort
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.annotation.Transactional

open class OutboxEventMessageRelayJob(
    eventRepository: BoxEventMessageRelayRepositoryPort,
    eventDeserializer: ApplicationEventDeserializer,
    eventPublisher: ApplicationEventPublisher,
    batchSize: Int,
) : BoxEventMessageRelay(
    eventRepository,
    eventDeserializer,
    eventPublisher,
    batchSize,
) {
    // TODO Move the polling interval to configuration
    @Scheduled(fixedDelay = 5000)
    @Transactional(timeout = 12)
    open fun pullMessagesFromOutbox() {
        execute()
    }
}
