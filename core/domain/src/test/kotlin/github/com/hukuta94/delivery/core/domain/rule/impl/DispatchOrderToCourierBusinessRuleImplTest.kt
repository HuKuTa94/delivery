package github.com.hukuta94.delivery.core.domain.rule.impl

import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.busyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.createdOrder
import github.com.hukuta94.delivery.core.domain.common.maxLocation
import github.com.hukuta94.delivery.core.domain.common.minLocation
import github.com.hukuta94.delivery.core.domain.common.location
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class DispatchOrderToCourierBusinessRuleImplTest {

    // System Under Testing
    private val rule = DispatchOrderToCourierBusinessRuleImpl()

    @Test
    fun `assignOrderToMostSuitableCourier does nothing when no free couriers`() {
        val order = createdOrder()
        val couriers = listOf(busyCourier())

        assertDoesNotThrow {
            rule.execute(order, couriers)
        }
    }

    @Test
    fun `assignOrderToMostSuitableCourier assigns order to only free courier and changes his status`() {
        val order = createdOrder()
        val freeCourier = freeCourier()
        val busyCourier = busyCourier()
        val couriers = listOf(freeCourier, busyCourier)

        freeCourier.status shouldBe CourierStatus.FREE
        rule.execute(order, couriers)

        freeCourier.status shouldBe CourierStatus.BUSY
    }

    @Test
    fun `assignOrderToMostSuitableCourier assigns order to most suitable and nearest courier`() {
        val order = createdOrder(
            location = maxLocation()
        )
        val fartherCourier = freeCourier(
            location = minLocation()
        )
        val nearestCourier = freeCourier(
            location = location(4, 4)
        )
        val couriers = listOf(fartherCourier, nearestCourier)

        rule.execute(order, couriers)

        assertSoftly {
            nearestCourier.status shouldBe CourierStatus.BUSY
            fartherCourier.status shouldBe CourierStatus.FREE
        }
    }

    @Test
    fun `assignOrderToMostSuitableCourier changes order status to assigned`() {
        val order = createdOrder()
        val freeCourier = freeCourier()
        val couriers = listOf(freeCourier)

        rule.execute(order, couriers)

        order.status shouldBe OrderStatus.ASSIGNED
    }

    @Test
    fun `assignOrderToMostSuitableCourier changes courier id in order`() {
        val order = createdOrder()
        val freeCourier = freeCourier()
        val couriers = listOf(freeCourier)

        order.courierId shouldNotBe freeCourier.id
        rule.execute(order, couriers)

        order.courierId shouldBe freeCourier.id
    }
}
