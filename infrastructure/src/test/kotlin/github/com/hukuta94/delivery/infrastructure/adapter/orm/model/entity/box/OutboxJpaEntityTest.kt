package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity.box

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventSerializer
import github.com.hukuta94.delivery.core.domain.order.OrderAssignedDomainEvent
import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
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
        whenever(domainEventSerializer.serialize(any()))
            .thenReturn(serializedDomainEvent)

        // When
        val outboxEntity = OutboxJpaEntity.fromEvent(domainEvent, domainEventSerializer)

        // Then
        assertSoftly {
            outboxEntity.id shouldBe domainEvent.id
            outboxEntity.eventType shouldBe "order.OrderAssignedDomainEvent"
            outboxEntity.payload shouldBe serializedDomainEvent
            outboxEntity.createdAt shouldNotBe null
            outboxEntity.processedAt shouldBe null
        }
    }
}