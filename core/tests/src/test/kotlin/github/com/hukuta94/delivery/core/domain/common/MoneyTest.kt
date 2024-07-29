package github.com.hukuta94.delivery.core.domain.common

import github.com.hukuta94.delivery.core.port.Exchange
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.util.stream.Stream

internal class MoneyTest {

    @Test
    fun `zero amount is valid`() {
        assertDoesNotThrow { Money() }
    }

    @Test
    fun `negative amount is invalid`() {
        val negativeAmount = -1

        assertThrows<IllegalArgumentException> { Money(negativeAmount) }.message shouldBe
            "Expected amount must be greater zero. Actual amount is $negativeAmount"
    }

    @ParameterizedTest
    @MethodSource("sameCurrencies")
    fun `adds in same currency`(currency: Currency) {
        val money1 = Money(1, currency)
        val money2 = Money(4, currency)
        val expected = Money(5, currency)

        `operation with money in same currencies is possible`(expected) { money1 + money2 }
    }

    @ParameterizedTest
    @MethodSource("sameCurrencies")
    fun `deducts in same currency`(currency: Currency) {
        val money1 = Money(4, currency)
        val money2 = Money(1, currency)
        val expected = Money(3, currency)

        `operation with money in same currencies is possible`(expected) { money1 - money2 }
    }

    private fun `operation with money in same currencies is possible`(
        expected: Money,
        operation: () -> Money,
    ) {
        operation.invoke() shouldBe expected
    }

    @ParameterizedTest
    @MethodSource("differentCurrencies")
    fun `adds in different currencies is impossible`(currency1: Currency, currency2: Currency) {
        val money1 = Money(1, currency1)
        val money2 = Money(1, currency2)

        `operation with money in different currencies is impossible`(money1, money2) { money1 + money2 }
    }

    @ParameterizedTest
    @MethodSource("differentCurrencies")
    fun `deducts in different currencies is impossible`(currency1: Currency, currency2: Currency) {
        val money1 = Money(1, currency1)
        val money2 = Money(1, currency2)

        `operation with money in different currencies is impossible`(money1, money2) { money1 - money2 }
    }

    private fun `operation with money in different currencies is impossible`(
        money1: Money,
        money2: Money,
        operation: () -> Money,
    ) {
        assertThrows<IllegalArgumentException> { operation() }.message shouldBe
            "Impossible to operate with amounts of different currencies: ${money1.currency} and ${money2.currency}"
    }

    /**
     * This test verifies the fact of converting origin currency into target.
     * It does not matter the correctness of the exchange rate.
     */
    @ParameterizedTest
    @MethodSource("allCurrencyCombinations")
    fun `conversion of money to currencies`(originCurrency: Currency, targetCurrency: Currency) {
        val originAmount = BigDecimal.ONE
        val origin = Money(originAmount, originCurrency)

        val result = origin.inCurrency(targetCurrency, FakeExchange())

        result.amount shouldNotBe originAmount
        result.currency shouldBe targetCurrency
    }

    private class FakeExchange : Exchange {
        override fun rate(origin: Currency, target: Currency): BigDecimal {
            return BigDecimal.TEN
        }
    }

    companion object {

        @JvmStatic
        private fun sameCurrencies(): Stream<Arguments> {
            return Currency.values()
                .map { Arguments.of(it) }
                .stream()
        }

        @JvmStatic
        private fun differentCurrencies(): Stream<Arguments> {
            val currencies = Currency.values()

            return currencies.flatMap { origin ->
                currencies.filterNot { target ->
                    origin == target
                }.map { target ->
                    Arguments.of(origin, target)
                }
            }.stream()
        }

        @JvmStatic
        private fun allCurrencyCombinations(): Stream<Arguments> {
            val currencies = Currency.values()

            return currencies.flatMap { origin ->
                currencies.map { target ->
                    Arguments.of(origin, target)
                }
            }.stream()
        }
    }
}

