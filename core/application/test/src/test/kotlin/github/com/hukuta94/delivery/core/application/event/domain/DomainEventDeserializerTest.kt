package github.com.hukuta94.delivery.core.application.event.domain

import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedDomainEvent
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedDomainEvent
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import java.util.*

internal class DomainEventDeserializerTest {

    private val sut = DomainEventDeserializer()

    @Test
    fun `deserializes order assigned domain event from string`() {
        // Given
        val domainEvent = OrderAssignedDomainEvent(
            orderId = UUID.randomUUID(),
            courierId = UUID.randomUUID(),
        )
        val serializedDomainEvent = """
            {
                "id":"${domainEvent.id}",
                "orderId":"${domainEvent.orderId}",
                "courierId":"${domainEvent.courierId}"
            }
        """.withoutSpaces()

        // When
        val actualDeserializedDomainEvent = sut.deserialize(
            serializedEvent = serializedDomainEvent,
            eventClassType = DomainEventClassType(domainEvent::class),
        )

        // Then
        actualDeserializedDomainEvent shouldBe domainEvent
    }

    @Test
    fun `deserializes order completed domain event from string`() {
        // Given
        val domainEvent = OrderCompletedDomainEvent(
            orderId = UUID.randomUUID(),
        )
        val serializedDomainEvent = """
            {
                "orderId":"${domainEvent.orderId}",
                "id":"${domainEvent.id}"
            }
        """.withoutSpaces()

        // When
        val actualDeserializedDomainEvent = sut.deserialize(
            serializedEvent = serializedDomainEvent,
            eventClassType = DomainEventClassType(domainEvent::class),
        )

        // Then
        actualDeserializedDomainEvent shouldBe domainEvent
    }

    private fun String.withoutSpaces() = this.trimIndent().replace(SPACES_REGEX, "")

    companion object {
        private val SPACES_REGEX = Regex("\\s+")
    }
}