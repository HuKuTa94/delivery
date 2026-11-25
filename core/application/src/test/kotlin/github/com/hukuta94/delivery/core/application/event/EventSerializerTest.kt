package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedEvent
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedEvent
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import java.util.*

class EventSerializerTest : StringSpec({

    val sut = EventSerializer()

    "serializes order assigned domain event into string" {
        // Given
        val domainEvent = OrderAssignedEvent(
            orderId = UUID.randomUUID(),
            courierId = UUID.randomUUID(),
        )

        // When
        val actual = sut.serialize(domainEvent)

        // Then
        assertSoftly {
            actual shouldContain "\"eventId\":\"${domainEvent.eventId}\""
            actual shouldContain "\"orderId\":\"${domainEvent.orderId}\""
            actual shouldContain "\"courierId\":\"${domainEvent.courierId}\""
        }
    }

    "serializes order completed domain event into string" {
        // Given
        val domainEvent = OrderCompletedEvent(
            orderId = UUID.randomUUID()
        )

        // When
        val actual = sut.serialize(domainEvent)

        // Then
        assertSoftly {
            actual shouldContain "\"eventId\":\"${domainEvent.eventId}\""
            actual shouldContain "\"orderId\":\"${domainEvent.orderId}\""
        }
    }

    "serializes basket confirmed integration event into string" {
        // Given
        val event = BasketConfirmedEvent(
            basketId = UUID.randomUUID(),
            street = "country,city,street,building,zip",
        )

        // When
        val actual = sut.serialize(event)

        // Then
        assertSoftly {
            actual shouldContain "\"eventId\":\"${event.eventId}\""
            actual shouldContain "\"basketId\":\"${event.basketId}\""
            actual shouldContain "\"street\":\"${event.street}\""
        }
    }
})
