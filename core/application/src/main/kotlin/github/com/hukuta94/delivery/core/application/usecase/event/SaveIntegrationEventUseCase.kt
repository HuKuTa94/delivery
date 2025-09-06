package github.com.hukuta94.delivery.core.application.usecase.event

import github.com.hukuta94.delivery.core.application.usecase.UseCase
import github.com.hukuta94.delivery.core.domain.IntegrationEvent

interface SaveIntegrationEventUseCase : UseCase {

    fun execute(event: IntegrationEvent)
}