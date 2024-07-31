package github.com.hukuta94.delivery.core.application.port.repository.event

import github.com.hukuta94.delivery.core.domain.IntegrationEvent

interface InboxEventRepositoryPort {

    fun saveIntegrationEvent(integrationEvent: IntegrationEvent)
}