package github.com.hukuta94.delivery.core.application.usecase.event

import github.com.hukuta94.delivery.core.application.port.repository.event.InboxEventRepositoryPort
import github.com.hukuta94.delivery.core.application.usecase.UseCase
import github.com.hukuta94.delivery.core.domain.DomainEvent
import org.slf4j.LoggerFactory

class SaveIntegrationEventUseCase(
    private val inboxEventRepositoryPort: InboxEventRepositoryPort,
) : UseCase {
    operator fun invoke(event: DomainEvent) {
        val saved = inboxEventRepositoryPort.saveIntegrationEvent(event)
        if (saved) {
            LOG.info("Integration event with id: ${event.eventId} was saved to the inbox")
        } else {
            LOG.info("Integration event with id: ${event.eventId} already exists, skipped")
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(SaveIntegrationEventUseCase::class.java)
    }
}
