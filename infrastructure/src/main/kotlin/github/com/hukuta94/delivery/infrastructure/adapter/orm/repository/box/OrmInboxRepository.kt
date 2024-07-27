package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.box

import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEvent
import github.com.hukuta94.delivery.core.port.repository.box.InboxRepository
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter.IntegrationEventConverter
import github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.jpa.InboxJpaRepository

class OrmInboxRepository(
    private val inboxJpaRepository: InboxJpaRepository,
    private val integrationEventConverter: IntegrationEventConverter,
) : InboxRepository {

    override fun saveIntegrationEvent(integrationEvent: IntegrationEvent) {
        val inboxMessage = integrationEventConverter.toInboxJpaEntity(integrationEvent)
        inboxJpaRepository.save(inboxMessage)
    }
}