package github.com.hukuta94.delivery.infrastructure.orm.spring.repository.event

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedDomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.spring.inoutbox.messagesInStatus
import github.com.hukuta94.delivery.infrastructure.orm.spring.integration.SpringJpaIntegrationSpec
import io.kotest.matchers.shouldBe
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.util.UUID

internal class SpringJpaOutboxRepositoryIT : SpringJpaIntegrationSpec() {
    init {
        val adapter = SpringJpaOutboxEventRepositoryAdapter(
            context.getBean(SpringJpaOutboxEventRepository::class.java),
            ApplicationEventSerializer(),
        )
        // FOR UPDATE SKIP LOCKED requires an active transaction.
        val transaction = TransactionTemplate(context.getBean(PlatformTransactionManager::class.java))

        "findMessagesInStatuses (FOR UPDATE SKIP LOCKED) respects batchSize and status filter" {
            repeat(5) {
                adapter.saveDomainEvents(listOf(OrderCompletedDomainEvent(orderId = UUID.randomUUID())))
            }

            val found = transaction.messagesInStatus(adapter, BoxEventMessageStatus.TO_BE_PROCESSED, batchSize = 3)

            found.size shouldBe 3
            found.map { it.status }.toSet() shouldBe setOf(BoxEventMessageStatus.TO_BE_PROCESSED)
        }
    }
}
