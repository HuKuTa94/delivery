package github.com.hukuta94.delivery.infrastructure.orm.springjpa.inoutbox

import github.com.hukuta94.delivery.core.application.event.ApplicationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.ApplicationEventPublisher
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.application.port.repository.event.BoxEventMessageRelayRepositoryPort
import github.com.hukuta94.delivery.core.domain.FakeDomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.commons.OutboxEventMessageRelayJob
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.entity.event.OutboxEventMessageJpaEntity
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

internal class SpringInOutboxEventMessageRelayJobTest : StringSpec({

    lateinit var outboxEventJpaRepository: BoxEventMessageRelayRepositoryPort
    lateinit var eventPublisher: ApplicationEventPublisher
    lateinit var eventDeserializer: ApplicationEventDeserializer

    lateinit var sut: OutboxEventMessageRelayJob

    beforeTest {
        outboxEventJpaRepository = mock()
        eventPublisher = mock()
        eventDeserializer = mock()

        sut = OutboxEventMessageRelayJob(
            outboxEventJpaRepository,
            eventDeserializer,
            eventPublisher,
        )

        whenever(outboxEventJpaRepository.findMessagesInStatuses(any()))
            .thenReturn(listOf(OUTBOX_MESSAGE))
    }

    "happy path - must process messages successfully" {
        // When
        whenever(eventDeserializer.deserialize(any(), any())).thenReturn(mock())
        sut.pullMessagesFromOutbox()

        // Then
        assertSoftly {
            OUTBOX_MESSAGE.status shouldBe BoxEventMessageStatus.SUCCESS
            verify(outboxEventJpaRepository).saveAll(listOf(OUTBOX_MESSAGE))
        }
    }

    "must handle deserialization error" {
        // When
        whenever(eventDeserializer.deserialize(any(), any())).thenThrow(RuntimeException("Deserialization error"))
        sut.pullMessagesFromOutbox()

        // Then
        assertSoftly {
            OUTBOX_MESSAGE.status shouldBe BoxEventMessageStatus.CONVERSION_ERROR
            verify(outboxEventJpaRepository).saveAll(listOf(OUTBOX_MESSAGE))
        }
    }

    "must handle publication error" {
        // When
        whenever(eventDeserializer.deserialize(any(), any())).thenReturn(mock())
        whenever(eventPublisher.publish(any())).thenThrow(IllegalStateException("Publication error"))
        sut.pullMessagesFromOutbox()

        // Then
        assertSoftly {
            OUTBOX_MESSAGE.status shouldBe BoxEventMessageStatus.DELIVERY_ERROR
            verify(outboxEventJpaRepository).saveAll(listOf(OUTBOX_MESSAGE))
        }
    }
}) {
    companion object {
        private val OUTBOX_MESSAGE = OutboxEventMessageJpaEntity().apply {
            eventType = FakeDomainEvent::class.java
            payload = "serialized data"
            processedAt = null
        }
    }
}