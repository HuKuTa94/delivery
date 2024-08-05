package github.com.hukuta94.delivery.core.application.event.domain

import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedDomainEvent
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedDomainEvent
import io.kotlintest.assertSoftly
import io.kotlintest.matchers.string.shouldContain
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
}