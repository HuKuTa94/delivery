package github.com.hukuta94.delivery.core.service.impl

import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.busyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.newOrder
import github.com.hukuta94.delivery.core.domain.service.impl.DispatchServiceImpl
import github.com.hukuta94.delivery.core.domain.common.maximalLocation
import github.com.hukuta94.delivery.core.domain.common.minimalLocation
import github.com.hukuta94.delivery.core.domain.common.newLocation
import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class DispatchServiceImplTest {

    private val sut = DispatchServiceImpl()

    @Test
    fun `assignOrderToMostSuitableCourier does nothing when no free couriers`() {
        val order = newOrder()
        val couriers = listOf(busyCourier())

        assertDoesNotThrow {
            sut.assignOrderToMostSuitableCourier(order, couriers)
        }
    }

    @Test
    fun `assignOrderToMostSuitableCourier assigns order to only free courier and changes his status`() {
        val order = newOrder()
        val freeCourier = freeCourier()
        val busyCourier = busyCourier()
        val couriers = listOf(freeCourier, busyCourier)

        freeCourier.status shouldBe CourierStatus.FREE
        sut.assignOrderToMostSuitableCourier(order, couriers)

        freeCourier.status shouldBe CourierStatus.BUSY
    }

    @Test
    fun `assignOrderToMostSuitableCourier assigns order to most suitable and nearest courier`() {
        val order = newOrder(
            location = maximalLocation()
        )
        val fartherCourier = freeCourier(
            location = minimalLocation()
        )
        val nearestCourier = freeCourier(
            location = newLocation(4, 4)
        )
        val couriers = listOf(fartherCourier, nearestCourier)

        sut.assignOrderToMostSuitableCourier(order, couriers)

        assertSoftly {
            nearestCourier.status shouldBe CourierStatus.BUSY
            fartherCourier.status shouldBe CourierStatus.FREE
        }
    }

    @Test
    fun `assignOrderToMostSuitableCourier changes order status to assigned`() {
        val order = newOrder()
        val freeCourier = freeCourier()
        val couriers = listOf(freeCourier)

        sut.assignOrderToMostSuitableCourier(order, couriers)

        order.status shouldBe OrderStatus.ASSIGNED
    }

    @Test
    fun `assignOrderToMostSuitableCourier changes courier id in order`() {
        val order = newOrder()
        val freeCourier = freeCourier()
        val couriers = listOf(freeCourier)

        order.courierId shouldNotBe freeCourier.id
        sut.assignOrderToMostSuitableCourier(order, couriers)

        order.courierId shouldBe freeCourier.id
    }
}
