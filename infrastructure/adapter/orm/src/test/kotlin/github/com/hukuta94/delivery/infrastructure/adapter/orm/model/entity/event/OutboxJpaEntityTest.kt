package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.event

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventClassType
import github.com.hukuta94.delivery.core.application.event.domain.DomainEventSerializer
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedDomainEvent
import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.LocalDateTime
import java.util.*

class OutboxJpaEntityTest {

    // Mocks
    private val domainEventSerializer: DomainEventSerializer = Mockito.mock()

    @Test
    fun `creates outbox entity from domain event`() {
        // Given
        val serializedDomainEvent = "serialized domain event"
        val domainEvent = OrderAssignedDomainEvent(
            orderId = UUID.randomUUID(),
            courierId = UUID.randomUUID(),
        )
        val eventType = DomainEventClassType(domainEvent::class)
        whenever(domainEventSerializer.serialize(any()))
            .thenReturn(serializedDomainEvent)

        // When
        val outboxEntity = OutboxEventJpaEntity.fromEvent(domainEvent, domainEventSerializer, LocalDateTime.now())

        // Then
        assertSoftly {
            outboxEntity.id shouldBe domainEvent.id
            outboxEntity.eventType.value shouldBe eventType.value
            outboxEntity.payload shouldBe serializedDomainEvent
            outboxEntity.createdAt shouldNotBe null
            outboxEntity.processedAt shouldBe null
        }
    }
}