package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedDomainEvent
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedDomainEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.*

class DomainEventDeserializerTest : StringSpec({

    val sut = ApplicationEventDeserializer()

    "deserializes order assigned domain event from string" {
        // Given
        val domainEvent = OrderAssignedDomainEvent(
            orderId = UUID.randomUUID(),
            courierId = UUID.randomUUID(),
        )
        val serializedDomainEvent = """
            {
                "eventId":"${domainEvent.eventId}",
                "orderId":"${domainEvent.orderId}",
                "courierId":"${domainEvent.courierId}"
            }
        """.withoutSpaces()

        // When
        val actualDeserializedDomainEvent = sut.deserialize(
            serializedEvent = serializedDomainEvent,
            eventClassType = domainEvent::class.java,
        )

        // Then
        actualDeserializedDomainEvent shouldBe domainEvent
    }

    "deserializes order completed domain event from string" {
        // Given
        val domainEvent = OrderCompletedDomainEvent(
            orderId = UUID.randomUUID(),
        )
        val serializedDomainEvent = """
            {
                "orderId":"${domainEvent.orderId}",
                "eventId":"${domainEvent.eventId}"
            }
        """.withoutSpaces()

        // When
        val actualDeserializedDomainEvent = sut.deserialize(
            serializedEvent = serializedDomainEvent,
            eventClassType = domainEvent::class.java,
        )

        // Then
        actualDeserializedDomainEvent shouldBe domainEvent
    }
}) {
    companion object {
        private val SPACES_REGEX = Regex("\\s+")

        private fun String.withoutSpaces() = this.trimIndent().replace(SPACES_REGEX, "")
    }
}
