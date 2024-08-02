//TODO чистый domain не может зависеть от интерфейсов port, которые находятся в application layer.
// Подумать как орагнизовать этот класс и обмен валют

//package github.com.hukuta94.delivery.core.domain.common
//
//import github.com.hukuta94.delivery.core.application.port.Exchange
//import java.math.BigDecimal
//
//data class Money(
//    val amount: BigDecimal = BigDecimal.ZERO,
//    val currency: Currency = Currency.RUB,
//) {
//    constructor(
//        amount: Int,
//        currency: Currency = Currency.RUB,
//    ): this(BigDecimal(amount), currency)
//
//    constructor(
//        amount: Long,
//        currency: Currency = Currency.RUB,
//    ): this(BigDecimal(amount), currency)
//
//    constructor(
//        amount: Double,
//        currency: Currency = Currency.RUB,
//    ): this(BigDecimal(amount), currency)
//
//    constructor(
//        amount: String,
//        currency: Currency = Currency.RUB,
//    ): this(BigDecimal(amount), currency)
//
//    init {
//        if (amount < BigDecimal.ZERO) {
//            throw IllegalArgumentException(
//                "Expected amount must be greater zero. Actual amount is $amount"
//            )
//        }
//    }
//
//    fun inCurrency(currency: Currency, exchange: Exchange): Money {
//        val conversionRate = exchange.rate(this.currency, currency)
//
//        return Money(
//            amount = this.amount * conversionRate,
//            currency = currency,
//        )
//    }
//
//    operator fun plus(other: Money): Money {
//        if (this.isInOtherCurrency(other)) {
//            throwDifferentCurrenciesError(other)
//        }
//
//        val newAmount = this.amount + other.amount
//        return Money(newAmount, this.currency)
//    }
//
//    operator fun minus(other: Money): Money {
//        if (this.isInOtherCurrency(other)) {
//            throwDifferentCurrenciesError(other)
//        }
//
//        val newAmount = this.amount - other.amount
//        return Money(newAmount, this.currency)
//    }
//
//    private fun isInOtherCurrency(other: Money) = this.currency != other.currency
//
//    private fun throwDifferentCurrenciesError(other: Money) {
//        throw IllegalArgumentException(
//            "Impossible to operate with amounts of different currencies: ${this.currency} and ${other.currency}"
//        )
//    }
//}
