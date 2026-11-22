package github.com.hukuta94.delivery.core.application.usecase.event

import github.com.hukuta94.delivery.core.application.port.repository.event.InboxEventRepositoryPort
import github.com.hukuta94.delivery.core.application.usecase.UseCase
import github.com.hukuta94.delivery.core.domain.DomainEvent

class SaveIntegrationEventUseCase(
    private val inboxEventRepositoryPort: InboxEventRepositoryPort,
) : UseCase {
    operator fun invoke(event: DomainEvent) {
        inboxEventRepositoryPort.saveIntegrationEvent(event)
    }
}
