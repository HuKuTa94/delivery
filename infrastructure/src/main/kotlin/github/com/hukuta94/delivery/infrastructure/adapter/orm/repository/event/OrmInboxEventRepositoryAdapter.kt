package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository.event

import github.com.hukuta94.delivery.core.domain.IntegrationEvent
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEventSerializer
import github.com.hukuta94.delivery.core.application.port.repository.event.InboxEventRepositoryPort
import github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event.InboxEventJpaEntity
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class OrmInboxEventRepositoryAdapter(
    private val inboxJpaRepository: InboxEventJpaRepository,
    private val integrationEventSerializer: IntegrationEventSerializer
) : InboxEventRepositoryPort {

    override fun saveIntegrationEvent(integrationEvent: IntegrationEvent) {
        // Don't save duplicates
        val integrationEventExists = inboxJpaRepository.existsById(integrationEvent.id)
        if (integrationEventExists) {
            LOG.info(
                "Integration event with id: ${integrationEvent.id} skipped because it already exists"
            )
            return
        }

        val inboxMessage = InboxEventJpaEntity.fromEvent(
            integrationEvent,
            integrationEventSerializer,
            createdAt = LocalDateTime.now()
        )

        inboxJpaRepository.save(inboxMessage)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(OrmInboxEventRepositoryAdapter::class.java)
    }
}