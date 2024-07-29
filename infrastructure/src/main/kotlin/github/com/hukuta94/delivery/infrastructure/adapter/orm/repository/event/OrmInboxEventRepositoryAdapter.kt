package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.event

import github.com.hukuta94.delivery.core.domain.IntegrationEvent
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventSerializer
import github.com.hukuta94.delivery.core.port.repository.event.InboxEventRepositoryPort
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event.InboxEventJpaEntity

class OrmInboxEventRepositoryAdapter(
    private val inboxJpaRepository: InboxEventJpaRepository,
    private val integrationEventSerializer: IntegrationEventSerializer
) : InboxEventRepositoryPort {

    override fun saveIntegrationEvent(integrationEvent: IntegrationEvent) {
        val inboxMessage = InboxEventJpaEntity.fromEvent(integrationEvent, integrationEventSerializer)
        inboxJpaRepository.save(inboxMessage)
    }
}