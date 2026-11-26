package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.event

import github.com.hukuta94.delivery.core.application.event.EventSerializer
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedEvent
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.integration.KtormIntegrationSpec
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import java.util.UUID

internal class KtormBoxEventRepositoryIT : KtormIntegrationSpec() {
    init {
        "findMessagesInStatuses respects batchSize and the status filter" {
            val repository = KtormOutboxEventRepository(database, EventSerializer())
            repeat(5) {
                repository.saveDomainEvents(listOf(OrderCompletedEvent(orderId = UUID.randomUUID())))
            }

            val found = repository.findMessagesInStatuses(
                statuses = setOf(BoxEventMessageStatus.TO_BE_PROCESSED),
                batchSize = 3,
            )

            found.size shouldBe 3
            found.map { it.status }.toSet() shouldBe setOf(BoxEventMessageStatus.TO_BE_PROCESSED)
        }

        "findMessagesInStatuses returns empty when no message matches the status" {
            val repository = KtormOutboxEventRepository(database, EventSerializer())
            repository.saveDomainEvents(listOf(OrderCompletedEvent(orderId = UUID.randomUUID())))

            val found = repository.findMessagesInStatuses(
                statuses = setOf(BoxEventMessageStatus.SUCCESS),
                batchSize = 10,
            )

            found.size shouldBe 0
        }

        "saving no domain events adds no message" {
            // Given
            val repository = KtormOutboxEventRepository(
                database = database,
                eventSerializer = EventSerializer(),
            )

            // When
            val outcome = runCatching { repository.saveDomainEvents(emptyList()) }

            // Then
            assertSoftly {
                outcome.shouldBeSuccess()
                repository.findMessagesInStatuses(
                    statuses = BoxEventMessageStatus.entries.toSet(),
                    batchSize = 10,
                ).shouldBeEmpty()
            }
        }

        "saving no relay messages changes nothing" {
            // Given
            val repository = KtormOutboxEventRepository(
                database = database,
                eventSerializer = EventSerializer(),
            )
            repository.saveDomainEvents(listOf(OrderCompletedEvent(orderId = UUID.randomUUID())))

            // When
            val outcome = runCatching { repository.saveAll(emptyList()) }

            // Then
            assertSoftly {
                outcome.shouldBeSuccess()
                repository.findMessagesInStatuses(
                    statuses = setOf(BoxEventMessageStatus.TO_BE_PROCESSED),
                    batchSize = 10,
                ).size shouldBe 1
            }
        }
    }
}
