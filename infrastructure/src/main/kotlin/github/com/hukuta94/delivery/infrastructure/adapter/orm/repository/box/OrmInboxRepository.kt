package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.box

import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEvent
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventSerializer
import github.com.hukuta94.delivery.core.port.repository.box.InboxRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.box.InboxJpaEntity
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.jpa.InboxJpaRepository

class OrmInboxRepository(
    private val inboxJpaRepository: InboxJpaRepository,
    private val integrationEventSerializer: IntegrationEventSerializer
) : InboxRepository {

    override fun saveIntegrationEvent(integrationEvent: IntegrationEvent) {
        val inboxMessage = InboxJpaEntity.fromEvent(integrationEvent, integrationEventSerializer)
        inboxJpaRepository.save(inboxMessage)
    }
}