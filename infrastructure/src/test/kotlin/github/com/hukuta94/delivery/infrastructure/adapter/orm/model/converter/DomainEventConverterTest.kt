package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter

import github.com.hukuta94.delivery.core.application.event.domain.DomainEventSerializer
import github.com.hukuta94.delivery.core.domain.order.OrderAssignedDomainEvent
import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.util.*

internal class DomainEventConverterTest {

    // Mocks
    private val domainEventSerializer: DomainEventSerializer = mock()

    // System Under Testing
    private val sut = DomainEventConverter(domainEventSerializer)

    @Test
    fun `converts domain events into out box entity`() {
        // Given
        val serializedDomainEvent = "serialized domain event"
        val domainEvent = OrderAssignedDomainEvent(
                orderId = UUID.randomUUID(),
                courierId = UUID.randomUUID(),
            )
        whenever(domainEventSerializer.serialize(any()))
            .thenReturn(serializedDomainEvent)

        // When
        val outboxEntity = sut.toOutboxJpaEntities(listOf(domainEvent)).first()

        // Then
        assertSoftly {
            outboxEntity.id shouldBe domainEvent.id
            outboxEntity.type shouldBe "order.OrderAssignedDomainEvent"
            outboxEntity.content shouldBe serializedDomainEvent
            outboxEntity.createdAt shouldNotBe null
            outboxEntity.processedAt shouldBe null
        }
    }
}