package github.com.hukuta94.delivery.infrastructure.orm.job

import github.com.hukuta94.delivery.core.application.event.ApplicationEventDeserializer
import github.com.hukuta94.delivery.core.application.event.ApplicationEventPublisher
import github.com.hukuta94.delivery.core.domain.FakeDomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.model.entity.event.BoxEventStatus
import github.com.hukuta94.delivery.infrastructure.orm.model.entity.event.OutboxEventJpaEntity
import github.com.hukuta94.delivery.infrastructure.orm.repository.event.OutboxEventJpaRepository
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.mockito.kotlin.*

import org.springframework.data.domain.Page

internal class PollToPublishOutboxMessagesJobTest : StringSpec({

    lateinit var outboxEventJpaRepository: OutboxEventJpaRepository
    lateinit var eventPublisher: ApplicationEventPublisher
    lateinit var eventDeserializer: ApplicationEventDeserializer
    val page: Page<OutboxEventJpaEntity> = mock {
        on { content } doReturn listOf(OUTBOX_MESSAGE)
    }

    lateinit var sut: PublishOutboxMessagesJob

    beforeTest {
        outboxEventJpaRepository = mock()
        eventPublisher = mock()
        eventDeserializer = mock()

        sut = PublishOutboxMessagesJob(
            outboxEventJpaRepository,
            eventDeserializer,
            eventPublisher,
        )

        whenever(outboxEventJpaRepository.findAllByStatusIn(any(), any()))
            .thenReturn(page)
    }

    "happy path - must process messages successfully" {
        // When
        whenever(eventDeserializer.deserialize(any(), any())).thenReturn(mock())
        sut.pullMessagesFromOutbox()

        // Then
        assertSoftly {
            OUTBOX_MESSAGE.status shouldBe BoxEventStatus.SUCCESSFULLY
            verify(outboxEventJpaRepository).saveAll(listOf(OUTBOX_MESSAGE))
        }
    }

    "must handle deserialization error" {
        // When
        whenever(eventDeserializer.deserialize(any(), any())).thenThrow(RuntimeException("Deserialization error"))
        sut.pullMessagesFromOutbox()

        // Then
        assertSoftly {
            OUTBOX_MESSAGE.status shouldBe BoxEventStatus.CONVERSION_ERROR
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
            OUTBOX_MESSAGE.status shouldBe BoxEventStatus.DELIVERY_ERROR
            verify(outboxEventJpaRepository).saveAll(listOf(OUTBOX_MESSAGE))
        }
    }
}) {
    companion object {
        private val OUTBOX_MESSAGE = OutboxEventJpaEntity().apply {
            eventType = FakeDomainEvent::class.java
            payload = "serialized data"
            processedAt = null
        }
    }
}