package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.application.event.DomainEventSerializer.Companion.DOMAIN_EVENT_PACKAGE
import github.com.hukuta94.delivery.core.domain.order.OrderAssignedDomainEvent
import github.com.hukuta94.delivery.core.domain.order.OrderCompletedDomainEvent
import io.kotlintest.assertSoftly
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test

import java.util.*

internal class DomainEventSerializerTest {

    private val sut = DomainEventSerializer()

    @Test
    fun `serializes order assigned domain event into string`() {
        // Given
        val domainEvent = OrderAssignedDomainEvent(
            orderId = UUID.randomUUID(),
            courierId = UUID.randomUUID(),
        )

        // When
        val actualSerializedDomainEvent = sut.serialize(domainEvent)

        // Then
        assertSoftly {
            actualSerializedDomainEvent shouldContain "\"id\":\"${domainEvent.id}\""
            actualSerializedDomainEvent shouldContain "\"orderId\":\"${domainEvent.orderId}\""
            actualSerializedDomainEvent shouldContain "\"courierId\":\"${domainEvent.courierId}\""
        }
    }

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

        val cutDomainClassType = requireNotNull(
            OrderAssignedDomainEvent::class.qualifiedName?.substringAfter(DOMAIN_EVENT_PACKAGE)
        )

        // When
        val actualDeserializedDomainEvent = sut.deserialize(
            serializedDomainEvent = serializedDomainEvent,
            type = cutDomainClassType,
        )

        // Then
        actualDeserializedDomainEvent shouldBe domainEvent
    }

    @Test
    fun `serializes order completed domain event into string`() {
        // Given
        val domainEvent = OrderCompletedDomainEvent(
            orderId = UUID.randomUUID()
        )

        // When
        val actualSerializedDomainEvent = sut.serialize(domainEvent)

        // Then
        assertSoftly {
            actualSerializedDomainEvent shouldContain "\"id\":\"${domainEvent.id}\""
            actualSerializedDomainEvent shouldContain "\"orderId\":\"${domainEvent.orderId}\""
        }
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

        val cutDomainClassType = requireNotNull(
            OrderCompletedDomainEvent::class.qualifiedName?.substringAfter(DOMAIN_EVENT_PACKAGE)
        )


        // When
        val actualDeserializedDomainEvent = sut.deserialize(
            serializedDomainEvent = serializedDomainEvent,
            type = cutDomainClassType,
        )

        // Then
        actualDeserializedDomainEvent shouldBe domainEvent
    }

    private fun String.withoutSpaces() = this.trimIndent().replace(SPACES_REGEX, "")

    companion object {
        private val SPACES_REGEX = Regex("\\s+")
    }
}