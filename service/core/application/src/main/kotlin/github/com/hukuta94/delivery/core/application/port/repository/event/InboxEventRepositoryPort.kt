package github.com.hukuta94.delivery.core.application.port.repository.event

import github.com.hukuta94.delivery.core.domain.DomainEvent

interface InboxEventRepositoryPort {

    /**
     * Stores an integration event in the inbox unless one with the same event id is already stored.
     *
     * @return `true` if the event was stored, `false` if one with the same event id already exists
     */
    fun saveIntegrationEvent(integrationEvent: DomainEvent): Boolean
}
