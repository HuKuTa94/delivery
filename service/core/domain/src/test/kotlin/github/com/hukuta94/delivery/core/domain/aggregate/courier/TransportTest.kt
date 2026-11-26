package github.com.hukuta94.delivery.core.domain.aggregate.courier

import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.collection
import io.kotest.property.exhaustive.enum

class TransportTest : StringSpec({

    "every transport is found by its code" {
        checkAll(Exhaustive.enum<Transport>()) { transport ->
            // When
            val result = Transport.of(transport.name)

            // Then
            result shouldBeRight transport
        }
    }

    "transport is not found by an unknown code" {
        checkAll(Exhaustive.collection(listOf("BIKE", "", " "))) { code ->
            // When
            val result = Transport.of(code)

            // Then
            result.shouldBeLeft().also {
                it::class shouldBe Transport.Error.UnknownTransport::class
                it.message shouldBe
                        "Transport \"$code\" is unknown. " +
                        "Known transports are ${Transport.entries.joinToString()}"
            }
        }
    }

    "transport code must be written exactly as the transport name" {
        // Given
        val code = Transport.BICYCLE.name.lowercase()

        // When
        val result = Transport.of(code)

        // Then
        result.shouldBeLeft()::class shouldBe Transport.Error.UnknownTransport::class
    }
})
