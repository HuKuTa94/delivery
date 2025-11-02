package github.com.hukuta94.delivery.infrastructure.orm.repository.event

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.port.repository.event.InboxEventRepositoryPort
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.model.entity.event.InboxEventJpaEntity
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class OrmInboxEventRepositoryAdapter(
    private val inboxJpaRepository: InboxEventJpaRepository,
    private val eventSerializer: ApplicationEventSerializer
) : InboxEventRepositoryPort {

    override fun saveIntegrationEvent(integrationEvent: DomainEvent) {
        // Don't save duplicates
        val integrationEventExists = inboxJpaRepository.existsById(integrationEvent.eventId)
        if (integrationEventExists) {
            LOG.info(
                "Integration event with id: ${integrationEvent.eventId} skipped because it already exists"
            )
            return
        }

        val inboxMessage = InboxEventJpaEntity.fromEvent(
            integrationEvent,
            eventSerializer,
            createdAt = LocalDateTime.now()
        )

        inboxJpaRepository.save(inboxMessage)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(OrmInboxEventRepositoryAdapter::class.java)
    }
}