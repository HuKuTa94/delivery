package github.com.hukuta94.delivery.core.domain.aggregate.courier

import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class CourierNameTest {

    @Test
    fun `the name can not be empty or blank`() {
        fun assertCourierName(name: String) {
            assertThrows<IllegalArgumentException> { CourierName(name) }.message shouldBe
                "Courier name can not be empty or blank"
        }

        assertSoftly {
            assertCourierName("")
            assertCourierName(" ")
        }
    }

    @Test
    fun `the name length can not be too short`() {
        val name = shortInvalidName()

        assertThrows<IllegalArgumentException> { CourierName(name) }.message shouldBe
            "Courier name is too short. Minimal length must be $MIN_COURIER_NAME_LENGTH. Actual length of \"$name\" is ${name.length}"
    }

    private fun shortInvalidName(): String {
        return "A".repeat(MIN_COURIER_NAME_LENGTH - 1)
    }

    @Test
    fun `the name length can not be too long`() {
        val name = longInvalidName()

        assertThrows<IllegalArgumentException> { CourierName(name) }.message shouldBe
            "Courier name is too long. Maximal length must be $MAX_COURIER_NAME_LENGTH. Actual length of \"$name\" is ${name.length}"
    }

    private fun longInvalidName(): String {
        return "A".repeat(MAX_COURIER_NAME_LENGTH + 1)
    }

    @Test
    fun `the valid and not trimmed long name must be without spaces`() {
        val name = longValidNotTrimmedName()

        CourierName(name).value shouldBe longValidName()
    }

    private fun longValidNotTrimmedName(): String {
        val name = longValidName()
        return " $name "
    }

    private fun longValidName() = "A".repeat(MAX_COURIER_NAME_LENGTH)
}