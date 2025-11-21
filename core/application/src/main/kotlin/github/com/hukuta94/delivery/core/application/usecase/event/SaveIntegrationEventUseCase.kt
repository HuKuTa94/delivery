package github.com.hukuta94.delivery.core.application.usecase.event

import github.com.hukuta94.delivery.core.application.usecase.UseCase
import github.com.hukuta94.delivery.core.domain.DomainEvent

interface SaveIntegrationEventUseCase : UseCase {

    fun execute(event: DomainEvent)
}
