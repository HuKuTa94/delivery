package github.com.hukuta94.delivery.core.domain.aggregate.courier

import github.com.hukuta94.delivery.core.domain.CourierNameSpecification.MAX_COURIER_NAME_LENGTH
import github.com.hukuta94.delivery.core.domain.CourierNameSpecification.MIN_COURIER_NAME_LENGTH
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class CourierNameTest : StringSpec({

    "courier name must be created when within allowed length range and letters" {
        // Given
        val allowedLetters =
            ('A'..'Z') + ('a'..'z') + ('А'..'Я') + ('а'..'я')

        val strings = Arb.string(
            minSize = MIN_COURIER_NAME_LENGTH,
            maxSize = MAX_COURIER_NAME_LENGTH,
            codepoints = Arb.of(allowedLetters).map { Codepoint(it.code) },
        )

        checkAll(strings) { expected ->
            // When
            val result = CourierName.of(expected).shouldBeRight()

            // Then
            result.value shouldBe expected.trim()
        }
    }

    "courier name must not be blank" {
        // Given
        val strings = listOf("", " ", "\t", "\n")

        // Then
        strings.forEach { string ->
            val result = CourierName.of(string)
            result.shouldBeLeft().also {
                it::class shouldBe CourierName.Error.NameCanNotBeEmpty::class
                it.message shouldBe "Courier name can not be empty or blank"
            }
        }
    }

    "courier name must not be shorter than minimum length" {
        // Given
        val string = "a".repeat(MIN_COURIER_NAME_LENGTH - 1)

        // Then
        val result = CourierName.of(string)
        result.shouldBeLeft().also {
            it::class shouldBe CourierName.Error.NameTooShort::class
            it.message shouldBe "Courier name is too short. Minimal length must be $MIN_COURIER_NAME_LENGTH. Actual length of \"$string\" is ${string.length}"
        }
    }

    "courier name must not be longer than maximum length" {
        // Given
        val string = "a".repeat(MAX_COURIER_NAME_LENGTH + 1)

        // Then
        val result = CourierName.of(string)
        result.shouldBeLeft().also {
            it::class shouldBe CourierName.Error.NameTooLong::class
            it.message shouldBe "Courier name is too long. Maximal length must be $MAX_COURIER_NAME_LENGTH. Actual length of \"$string\" is ${string.length}"
        }
    }

    "courier name must trim leading and trailing whitespace" {
        // Given
        val string = "   Alex   "

        // When
        val result = CourierName.of(string).shouldBeRight()

        // Then
        result.value shouldBe "Alex"
    }

    "courier name must allow exactly minimum length" {
        // Given
        val string = "a".repeat(MIN_COURIER_NAME_LENGTH)

        // When
        val result = CourierName.of(string).shouldBeRight()

        // Then
        result.value shouldBe string
    }

    "courier name must allow exactly maximum length" {
        // Given
        val string = "a".repeat(MAX_COURIER_NAME_LENGTH)

        // When
        val result = CourierName.of(string).shouldBeRight()

        // Then
        result.value shouldBe string
    }
})