package github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.entity.event

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedDomainEvent
import github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.entity.event.OutboxEventMessageJpaEntity
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.*

class OutboxJpaEntityTest {

    // Mocks
    private val eventSerializer: ApplicationEventSerializer = mock()

    @Test
    fun `creates outbox entity from domain event`() {
        // Given
        val serializedDomainEvent = "serialized domain event"
        val domainEvent = OrderAssignedDomainEvent(
            orderId = UUID.randomUUID(),
            courierId = UUID.randomUUID(),
        )
        val eventType = domainEvent::class
        whenever(eventSerializer.serialize(any()))
            .thenReturn(serializedDomainEvent)

        // When
        val outboxEntity = OutboxEventMessageJpaEntity.fromEvent(domainEvent, eventSerializer, LocalDateTime.now())

        // Then
        assertSoftly {
            outboxEntity.id shouldBe domainEvent.eventId
            outboxEntity.eventType shouldBe eventType.java
            outboxEntity.payload shouldBe serializedDomainEvent
            outboxEntity.createdAt shouldNotBe null
            outboxEntity.processedAt shouldBe null
        }
    }
}