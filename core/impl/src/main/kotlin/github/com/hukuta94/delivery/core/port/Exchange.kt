package github.com.hukuta94.delivery.core.port

import github.com.hukuta94.delivery.core.domain.common.Currency
import java.math.BigDecimal

interface Exchange {

    /**
     * @param origin currency to be converted from
     * @param target currency to be converted into
     *
     * @return conversion rate origin currency to target currency
     */
    fun rate(origin: Currency, target: Currency): BigDecimal
}