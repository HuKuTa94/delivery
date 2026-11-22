package github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.event

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.application.event.basketConfirmedIntegrationDomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.commons.integration.concurrency.StartGate
import github.com.hukuta94.delivery.infrastructure.orm.commons.integration.concurrency.failuresByType
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.integration.KtormIntegrationSpec
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.table.InboxEventMessageTable
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

/**
 * Kafka delivers at least once, so the inbox gets the same integration event again, also from several consumer
 * threads at the same moment. The inbox must store it once and fail no caller. Races depend on timing: each race
 * runs [ROUNDS] rounds at the start gate, one fresh event per round, and the checks run once at the end.
 */
internal class KtormInboxEventRepositoryIT : KtormIntegrationSpec() {
    init {
        val sut = KtormInboxEventRepository(
            database = database,
            eventSerializer = ApplicationEventSerializer(),
        )

        "an integration event delivered again is stored once" {
            // Given
            val event = basketConfirmedIntegrationDomainEvent()
            sut.saveIntegrationEvent(event)

            // When
            val redelivery = runCatching { sut.saveIntegrationEvent(event) }

            // Then
            assertSoftly {
                redelivery.shouldBeSuccess()
                count(InboxEventMessageTable) shouldBe 1
            }
        }

        "an integration event saved by a few callers at once is stored once and fails none of them" {
            // Given
            val basketId = UUID.randomUUID()
            val events = List(ROUNDS) {
                basketConfirmedIntegrationDomainEvent(basketId = basketId)
            }

            // When
            val outcomes = events.flatMap { event ->
                saveAtOnce { sut.saveIntegrationEvent(event) }
            }

            // Then
            assertSoftly {
                count(InboxEventMessageTable) shouldBe ROUNDS
                outcomes.failuresByType().shouldBeEmpty()
            }
        }

        "distinct integration events saved at once are all stored" {
            // Given
            val rounds = List(ROUNDS) {
                List(THREADS) {
                    basketConfirmedIntegrationDomainEvent()
                }
            }

            // When
            val outcomes = rounds.flatMap { events ->
                saveAtOnce { saver -> sut.saveIntegrationEvent(events[saver]) }
            }

            // Then
            assertSoftly {
                count(InboxEventMessageTable) shouldBe ROUNDS * THREADS
                outcomes.failuresByType().shouldBeEmpty()
            }
        }
    }

    private fun saveAtOnce(save: (saver: Int) -> Unit) = StartGate.run(
        threads = THREADS,
        timeout = TIMEOUT,
        action = save,
    )

    private companion object {
        private const val ROUNDS = 20
        private const val THREADS = 8
        private val TIMEOUT = 5.seconds
    }
}
