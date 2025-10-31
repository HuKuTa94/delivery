package github.com.hukuta94.delivery.core.domain.rule.impl

import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.newBusyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.newFreeCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.newOrder
import github.com.hukuta94.delivery.core.domain.common.newLocationWithMaxCoords
import github.com.hukuta94.delivery.core.domain.common.newLocationWithMinCoords
import github.com.hukuta94.delivery.core.domain.common.newLocation
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class DispatchServiceImplTest {

    private val sut = DispatchOrderToCourierBusinessRuleImpl()

    @Test
    fun `assignOrderToMostSuitableCourier does nothing when no free couriers`() {
        val order = newOrder()
        val couriers = listOf(newBusyCourier())

        assertDoesNotThrow {
            sut.execute(order, couriers)
        }
    }

    @Test
    fun `assignOrderToMostSuitableCourier assigns order to only free courier and changes his status`() {
        val order = newOrder()
        val freeCourier = newFreeCourier()
        val busyCourier = newBusyCourier()
        val couriers = listOf(freeCourier, busyCourier)

        freeCourier.status shouldBe CourierStatus.FREE
        sut.execute(order, couriers)

        freeCourier.status shouldBe CourierStatus.BUSY
    }

    @Test
    fun `assignOrderToMostSuitableCourier assigns order to most suitable and nearest courier`() {
        val order = newOrder(
            location = newLocationWithMaxCoords()
        )
        val fartherCourier = newFreeCourier(
            location = newLocationWithMinCoords()
        )
        val nearestCourier = newFreeCourier(
            location = newLocation(4, 4)
        )
        val couriers = listOf(fartherCourier, nearestCourier)

        sut.execute(order, couriers)

        assertSoftly {
            nearestCourier.status shouldBe CourierStatus.BUSY
            fartherCourier.status shouldBe CourierStatus.FREE
        }
    }

    @Test
    fun `assignOrderToMostSuitableCourier changes order status to assigned`() {
        val order = newOrder()
        val freeCourier = newFreeCourier()
        val couriers = listOf(freeCourier)

        sut.execute(order, couriers)

        order.status shouldBe OrderStatus.ASSIGNED
    }

    @Test
    fun `assignOrderToMostSuitableCourier changes courier id in order`() {
        val order = newOrder()
        val freeCourier = newFreeCourier()
        val couriers = listOf(freeCourier)

        order.courierId shouldNotBe freeCourier.id
        sut.execute(order, couriers)

        order.courierId shouldBe freeCourier.id
    }
}
