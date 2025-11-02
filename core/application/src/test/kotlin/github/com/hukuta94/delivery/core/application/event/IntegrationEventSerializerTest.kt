package github.com.hukuta94.delivery.core.application.event

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldContain
import java.util.*

class IntegrationEventSerializerTest : StringSpec({

    val sut = ApplicationEventSerializer()

    "serializes basket confirmed integration event into string" {
        // Given
        val event = BasketConfirmedIntegrationDomainEvent(
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
