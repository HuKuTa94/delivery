package github.com.hukuta94.delivery.core.port.repository.box

import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEvent

interface InboxRepository {

    fun saveIntegrationEvent(integrationEvent: IntegrationEvent)
}