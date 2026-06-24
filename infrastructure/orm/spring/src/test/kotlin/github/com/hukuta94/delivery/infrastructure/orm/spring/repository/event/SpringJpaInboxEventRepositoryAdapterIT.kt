package github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedDomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.spring.integration.SpringJpaIntegrationSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

internal class SpringJpaInboxEventRepositoryAdapterIT : SpringJpaIntegrationSpec() {
    init {
        val inboxRepository = context.getBean(SpringJpaInboxEventRepository::class.java)
        val adapter = SpringJpaInboxEventRepositoryAdapter(inboxRepository, ApplicationEventSerializer())

        "deduplicates integration events by eventId" {
            val event = OrderCompletedDomainEvent(orderId = UUID.randomUUID())

            adapter.saveIntegrationEvent(event)
            adapter.saveIntegrationEvent(event)

            inboxRepository.count() shouldBe 1
        }

        "saves distinct integration events" {
            adapter.saveIntegrationEvent(OrderCompletedDomainEvent(orderId = UUID.randomUUID()))
            adapter.saveIntegrationEvent(OrderCompletedDomainEvent(orderId = UUID.randomUUID()))

            inboxRepository.count() shouldBe 2
        }
    }
}
