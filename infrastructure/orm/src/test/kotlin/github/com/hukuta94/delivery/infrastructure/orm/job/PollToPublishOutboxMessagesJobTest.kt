package github.com.hukuta94.delivery.infrastructure.orm.job

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventDeserializer
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventPublisher
import github.com.hukuta94.delivery.core.application.event.fakeDomainEventClassType
import github.com.hukuta94.delivery.infrastructure.orm.model.entity.event.OutboxEventJpaEntity
import github.com.hukuta94.delivery.infrastructure.orm.model.entity.event.BoxEventStatus
import github.com.hukuta94.delivery.infrastructure.orm.repository.event.OutboxEventJpaRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.*
import org.springframework.data.domain.Page


internal class PollToPublishOutboxMessagesJobTest {

    // Mocks
    private val outboxEventJpaRepository: OutboxEventJpaRepository = mock()
    private val domainEventPublisher: DomainEventPublisher = mock()
    private val domainEventDeserializer: DomainEventDeserializer = mock()
    
    // System under testing (SUT)
    private val sut = PublishOutboxMessagesJob(
        outboxEventJpaRepository,
        domainEventPublisher,
        domainEventDeserializer,
    )

    @Test
    fun `happy path - must process messages successfully`() {
        // Given
        val outboxMessage = OutboxEventJpaEntity().apply {
            eventType = fakeDomainEventClassType()
            payload = "serialized data"
            processedAt = null
        }
        val page: Page<OutboxEventJpaEntity> = mock {
            on { content } doReturn listOf(outboxMessage)
        }

        whenever(outboxEventJpaRepository.findAllByStatusIn(any(), any())).thenReturn(page)
        whenever(domainEventDeserializer.deserialize(any(), any())).thenReturn(mock())

        // When
        sut.pullMessagesFromOutbox()

        // Then
        assertSoftly {
            outboxMessage.status shouldBe BoxEventStatus.SUCCESSFULLY
            verify(outboxEventJpaRepository).saveAll(listOf(outboxMessage))
        }
    }

    @Test
    fun `must handle deserialization error`() {
        // Given
        val outboxMessage = OutboxEventJpaEntity().apply {
            eventType = fakeDomainEventClassType()
            payload = "serialized data"
            processedAt = null
        }
        val page: Page<OutboxEventJpaEntity> = mock {
            on { content } doReturn listOf(outboxMessage)
        }

        whenever(outboxEventJpaRepository.findAllByStatusIn(any(), any())).thenReturn(page)
        whenever(domainEventDeserializer.deserialize(any(), any())).thenThrow(RuntimeException("Deserialization error"))

        // When
        sut.pullMessagesFromOutbox()

        // Then
        assertSoftly {
            outboxMessage.status shouldBe BoxEventStatus.CONVERSION_ERROR
            verify(outboxEventJpaRepository).saveAll(listOf(outboxMessage))
        }
    }

    @Test
    fun `must handle publication error`() {
        // Given
        val outboxMessage = OutboxEventJpaEntity().apply {
            eventType = fakeDomainEventClassType()
            payload = "serialized data"
            processedAt = null
        }
        val page: Page<OutboxEventJpaEntity> = mock {
            on { content } doReturn listOf(outboxMessage)
        }

        whenever(outboxEventJpaRepository.findAllByStatusIn(any(), any())).thenReturn(page)
        whenever(domainEventDeserializer.deserialize(any(), any())).thenReturn(mock())
        whenever(domainEventPublisher.publish(any())).thenThrow(IllegalStateException("Publication error"))

        // When
        sut.pullMessagesFromOutbox()

        // Then
        assertSoftly {
            outboxMessage.status shouldBe BoxEventStatus.DELIVERY_ERROR
            verify(outboxEventJpaRepository).saveAll(listOf(outboxMessage))
        }
    }
}