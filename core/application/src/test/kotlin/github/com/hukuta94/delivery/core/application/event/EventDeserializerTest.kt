package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedEvent
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.*

class EventDeserializerTest : StringSpec({

    val sut = EventDeserializer()

    "deserializes order assigned domain event from string" {
        // Given
        val domainEvent = OrderAssignedEvent(
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
        val domainEvent = OrderCompletedEvent(
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

    "deserializes basket confirmed integration event from string" {
        // Given
        val event = BasketConfirmedEvent(
            basketId = UUID.randomUUID(),
            street = "country,city,street,building,zip",
        )
        val serializedEvent = """
            {
                "eventId":"${event.eventId}",
                "basketId":"${event.basketId}",
                "street":"${event.street}"
            }
        """.withoutSpaces()

        // When
        val result = sut.deserialize(
            serializedEvent = serializedEvent,
            eventClassType = event::class.java,
        )

        // Then
        result shouldBe event
    }
}) {
    companion object {
        private val SPACES_REGEX = Regex("\\s+")

        private fun String.withoutSpaces() = this.trimIndent().replace(SPACES_REGEX, "")
    }
}
