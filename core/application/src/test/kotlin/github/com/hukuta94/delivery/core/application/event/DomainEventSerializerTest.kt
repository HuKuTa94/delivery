package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedDomainEvent
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedDomainEvent
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import java.util.*

class DomainEventSerializerTest : StringSpec({

    val sut = ApplicationEventSerializer()

    "serializes order assigned domain event into string" {
        // Given
        val domainEvent = OrderAssignedDomainEvent(
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
        val domainEvent = OrderCompletedDomainEvent(
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
})
