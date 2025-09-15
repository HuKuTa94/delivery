package github.com.hukuta94.delivery.core.domain.aggregate.courier

import github.com.hukuta94.delivery.core.domain.CourierNameSpecification.MAX_COURIER_NAME_LENGTH
import github.com.hukuta94.delivery.core.domain.CourierNameSpecification.MIN_COURIER_NAME_LENGTH
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class CourierNameTest : StringSpec({

    "courier name must be created when within allowed length range" {
        // Given
        val arbitrary = Arb.string(
            minSize = MIN_COURIER_NAME_LENGTH,
            maxSize = MAX_COURIER_NAME_LENGTH,
        )

        checkAll(arbitrary) { expected ->
            // When
            val sut = CourierName(expected)

            // Then
            sut.value shouldBe expected.trim()
        }
    }

    "courier name must not be blank" {
        // Given
        val blanks = listOf("", " ", "\t", "\n")

        // Then
        blanks.forEach { blank ->
            shouldThrow<IllegalArgumentException> {
                CourierName(blank)
            }
        }
    }

    "courier name must not be shorter than minimum length" {
        // Given
        val tooShortStrings = listOf(
            "a".repeat(MIN_COURIER_NAME_LENGTH - 1),
            " ".repeat(MIN_COURIER_NAME_LENGTH - 1),
        )

        // Then
        tooShortStrings.forEach { string ->
            shouldThrow<IllegalArgumentException> {
                CourierName(string)
            }
        }
    }

    "courier name must not be longer than maximum length" {
        // Given
        val tooLongString = "a".repeat(MAX_COURIER_NAME_LENGTH + 1)

        // Then
        shouldThrow<IllegalArgumentException> {
            CourierName(tooLongString)
        }
    }

    "courier name must trim leading and trailing whitespace" {
        // Given
        val string = "   Alex   "

        // When
        val sut = CourierName(string)

        // Then
        sut.value shouldBe "Alex"
    }

    "courier name must allow exactly minimum length" {
        // Given
        val string = "a".repeat(MIN_COURIER_NAME_LENGTH)

        // When
        val sut = CourierName(string)

        // Then
        sut.value shouldBe string
    }

    "courier name must allow exactly maximum length" {
        // Given
        val string = "a".repeat(MAX_COURIER_NAME_LENGTH)

        // When
        val sut = CourierName(string)

        // Then
        sut.value shouldBe string
    }
})