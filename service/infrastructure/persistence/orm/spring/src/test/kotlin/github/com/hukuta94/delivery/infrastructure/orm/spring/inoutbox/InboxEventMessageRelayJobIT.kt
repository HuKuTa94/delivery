package github.com.hukuta94.delivery.infrastructure.orm.spring.inoutbox

import github.com.hukuta94.delivery.core.application.event.EventDeserializer
import github.com.hukuta94.delivery.core.application.event.EventPublisher
import github.com.hukuta94.delivery.core.application.event.EventSerializer
import github.com.hukuta94.delivery.core.application.event.handler.OrderCompletedEventHandler
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.application.port.BusProducerPort
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedEvent
import github.com.hukuta94.delivery.infrastructure.orm.commons.InboxEventMessageRelayJob
import github.com.hukuta94.delivery.infrastructure.orm.spring.integration.SpringJpaIntegrationSpec
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event.SpringJpaInboxEventRepository
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event.SpringJpaInboxEventRepositoryAdapter
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.util.UUID

internal class InboxEventMessageRelayJobIT : SpringJpaIntegrationSpec() {
    init {
        val inbox = SpringJpaInboxEventRepositoryAdapter(
            context.getBean(SpringJpaInboxEventRepository::class.java),
            EventSerializer(),
        )
        // The relay drains and persists the batch (JPA merge) in a single transaction, as in production.
        val transaction = TransactionTemplate(context.getBean(PlatformTransactionManager::class.java))

        // Real publishing path: the production OrderCompletedDomainEventHandler forwards to BusProducerPort,
        // mocked per test so the relay can be driven to success and failure without a real bus.
        fun relayJob(busProducer: BusProducerPort) = InboxEventMessageRelayJob(
            inbox,
            EventDeserializer(),
            EventPublisher(listOf(OrderCompletedEventHandler(busProducer))),
            batchSize = 50,
        )

        "drains, publishes and marks inbox messages SUCCESS against the real database" {
            val orderId = UUID.randomUUID()
            inbox.saveIntegrationEvent(OrderCompletedEvent(orderId = orderId))
            val busProducer = mock<BusProducerPort>()

            transaction.executeWithoutResult { relayJob(busProducer).pullMessagesFromInbox() }

            verify(busProducer).publishOrderCompletedDomainEvent(OrderCompletedEvent(orderId = orderId))
            assertSoftly {
                transaction.messagesInStatus(inbox, BoxEventMessageStatus.TO_BE_PROCESSED).shouldBeEmpty()
                transaction.messagesInStatus(inbox, BoxEventMessageStatus.SUCCESS).shouldHaveSize(1)
            }
        }

        "marks an inbox message DELIVERY_ERROR when publication fails (kept for retry)" {
            inbox.saveIntegrationEvent(OrderCompletedEvent(orderId = UUID.randomUUID()))
            val busProducer = mock<BusProducerPort>()
            whenever(busProducer.publishOrderCompletedDomainEvent(any()))
                .thenThrow(IllegalStateException("publication failed"))

            transaction.executeWithoutResult { relayJob(busProducer).pullMessagesFromInbox() }

            assertSoftly {
                transaction.messagesInStatus(inbox, BoxEventMessageStatus.SUCCESS).shouldBeEmpty()
                transaction.messagesInStatus(inbox, BoxEventMessageStatus.DELIVERY_ERROR).shouldHaveSize(1)
            }
        }
    }
}
