package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedDomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.integration.KtormIntegrationSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

internal class KtormBoxEventRepositoryIT : KtormIntegrationSpec() {
    init {
        "findMessagesInStatuses respects batchSize and the status filter" {
            val repository = KtormOutboxEventRepository(database, ApplicationEventSerializer())
            repeat(5) {
                repository.saveDomainEvents(listOf(OrderCompletedDomainEvent(orderId = UUID.randomUUID())))
            }

            val found = repository.findMessagesInStatuses(
                statuses = setOf(BoxEventMessageStatus.TO_BE_PROCESSED),
                batchSize = 3,
            )

            found.size shouldBe 3
            found.map { it.status }.toSet() shouldBe setOf(BoxEventMessageStatus.TO_BE_PROCESSED)
        }

        "findMessagesInStatuses returns empty when no message matches the status" {
            val repository = KtormOutboxEventRepository(database, ApplicationEventSerializer())
            repository.saveDomainEvents(listOf(OrderCompletedDomainEvent(orderId = UUID.randomUUID())))

            val found = repository.findMessagesInStatuses(
                statuses = setOf(BoxEventMessageStatus.SUCCESS),
                batchSize = 10,
            )

            found.size shouldBe 0
        }
    }
}
