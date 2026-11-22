package github.com.hukuta94.delivery.infrastructure.orm.spring.inoutbox

import github.com.hukuta94.delivery.core.application.event.ApplicationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.ApplicationEventPublisher
import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.event.handler.OrderCompletedDomainEventHandler
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.application.port.BusProducerPort
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedDomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.commons.OutboxEventMessageRelayJob
import github.com.hukuta94.delivery.infrastructure.orm.spring.integration.SpringJpaIntegrationSpec
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event.SpringJpaOutboxEventRepository
import github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event.SpringJpaOutboxEventRepositoryAdapter
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

internal class OutboxEventMessageRelayJobIT : SpringJpaIntegrationSpec() {
    init {
        // FOR UPDATE SKIP LOCKED + the status update of the same batch must run in one transaction.
        val transaction = TransactionTemplate(context.getBean(PlatformTransactionManager::class.java))
        val outbox = SpringJpaOutboxEventRepositoryAdapter(
            context.getBean(SpringJpaOutboxEventRepository::class.java),
            ApplicationEventSerializer(),
        )

        // Real publishing path: the production OrderCompletedDomainEventHandler forwards to BusProducerPort,
        // mocked per test (mocks are stateful) so the relay can be driven to success and failure without a real bus.
        fun relayJob(busProducer: BusProducerPort) = OutboxEventMessageRelayJob(
            outbox,
            ApplicationEventDeserializer(),
            ApplicationEventPublisher(listOf(OrderCompletedDomainEventHandler(busProducer))),
            batchSize = 50,
        )

        "drains, publishes and marks messages SUCCESS against the real database" {
            val orderId = UUID.randomUUID()
            outbox.saveDomainEvents(listOf(OrderCompletedDomainEvent(orderId = orderId)))
            val busProducer: BusProducerPort = mock()

            transaction.executeWithoutResult { relayJob(busProducer).pullMessagesFromOutbox() }

            verify(busProducer).publishOrderCompletedDomainEvent(OrderCompletedDomainEvent(orderId = orderId))
            assertSoftly {
                transaction.messagesInStatus(outbox, BoxEventMessageStatus.TO_BE_PROCESSED).shouldBeEmpty()
                transaction.messagesInStatus(outbox, BoxEventMessageStatus.SUCCESS).shouldHaveSize(1)
            }
        }

        "marks a message DELIVERY_ERROR when publication fails (kept for retry)" {
            outbox.saveDomainEvents(listOf(OrderCompletedDomainEvent(orderId = UUID.randomUUID())))
            val busProducer = mock<BusProducerPort>()
            whenever(busProducer.publishOrderCompletedDomainEvent(any()))
                .thenThrow(IllegalStateException("publication failed"))

            transaction.executeWithoutResult { relayJob(busProducer).pullMessagesFromOutbox() }

            assertSoftly {
                transaction.messagesInStatus(outbox, BoxEventMessageStatus.SUCCESS).shouldBeEmpty()
                transaction.messagesInStatus(outbox, BoxEventMessageStatus.DELIVERY_ERROR).shouldHaveSize(1)
            }
        }

        "does nothing and commits cleanly when the outbox is empty" {
            val job = OutboxEventMessageRelayJob(
                outbox,
                ApplicationEventDeserializer(),
                ApplicationEventPublisher(emptyList()),
                batchSize = 50,
            )

            transaction.executeWithoutResult { job.pullMessagesFromOutbox() }

            val result = transaction.execute {
                outbox.findMessagesInStatuses(BoxEventMessageStatus.entries.toSet(), 50)
            }.orEmpty()

            result.shouldBeEmpty()
        }
    }
}
