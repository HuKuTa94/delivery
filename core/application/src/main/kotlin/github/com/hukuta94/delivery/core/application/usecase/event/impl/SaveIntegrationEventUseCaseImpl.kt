package github.com.hukuta94.delivery.core.application.usecase.event.impl

import github.com.hukuta94.delivery.core.application.usecase.event.SaveIntegrationEventUseCase
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.core.application.port.repository.event.InboxEventRepositoryPort

class SaveIntegrationEventUseCaseImpl(
    private val inboxEventRepositoryPort: InboxEventRepositoryPort,
) : SaveIntegrationEventUseCase {

    override fun execute(event: DomainEvent) {
        inboxEventRepositoryPort.saveIntegrationEvent(event)
    }
}
