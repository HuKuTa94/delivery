package github.com.hukuta94.delivery.core.application.event

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.*

class IntegrationEventDeserializerTest : StringSpec({

    val sut = ApplicationEventDeserializer()

    "deserializes basket confirmed integration event from string" {
        // Given
        val event = BasketConfirmedIntegrationDomainEvent(
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
