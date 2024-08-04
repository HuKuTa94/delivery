package github.com.hukuta94.delivery.core.service.impl

import github.com.hukuta94.delivery.core.domain.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.courier.busyCourier
import github.com.hukuta94.delivery.core.domain.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.courier.newCourier
import github.com.hukuta94.delivery.core.domain.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.order.assignedOrder
import github.com.hukuta94.delivery.core.domain.order.newOrder
import github.com.hukuta94.delivery.core.domain.service.impl.CompleteOrderServiceImpl
import github.com.hukuta94.delivery.core.domain.common.newLocation
import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test

internal class CompleteOrderServiceImplTest {

    // System Under Testing
    private val sut = CompleteOrderServiceImpl()

    @Test
    fun `must return false if order is assigned to a different courier`() {
        val courier = freeCourier()
        val order = assignedOrder(courier = newCourier())

        val result = sut.execute(order, courier)

        result shouldBe false
    }

    @Test
    fun `must return false if order status is not ASSIGNED`() {
        val courier = freeCourier()
        val order = newOrder()

        val result = sut.execute(order, courier)

        result shouldBe false
    }

    @Test
    fun `must return false if courier has not reached the order location`() {
        val courier = busyCourier(
            location = newLocation(1, 1)
        )
        val order = assignedOrder(
            location = newLocation(2, 2),
            courier = courier
        )

        val result = sut.execute(order, courier)

        result shouldBe false
    }

    @Test
    fun `must return true and complete order if all conditions are met`() {
        val sameLocation = newLocation(1, 1)
        val courier = busyCourier(
            location = sameLocation
        )
        val order = assignedOrder(
            courier = courier,
            location = sameLocation
        )

        val result = sut.execute(order, courier)

        assertSoftly {
            result shouldBe true
            courier.status shouldBe CourierStatus.FREE
            order.status shouldBe OrderStatus.COMPLETED
        }
    }
}
