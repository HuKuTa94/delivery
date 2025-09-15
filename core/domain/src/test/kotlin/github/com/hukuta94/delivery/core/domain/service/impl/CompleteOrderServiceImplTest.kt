package github.com.hukuta94.delivery.core.service.impl

import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.newBusyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.newFreeCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.newCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.newAssignedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.newOrder
import github.com.hukuta94.delivery.core.domain.service.impl.CompleteOrderServiceImpl
import github.com.hukuta94.delivery.core.domain.common.newLocation
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class CompleteOrderServiceImplTest {

    // System Under Testing
    private val sut = CompleteOrderServiceImpl()

    @Test
    fun `must return false if order is assigned to a different courier`() {
        val courier = newFreeCourier()
        val order = newAssignedOrder(courier = newCourier())

        val result = sut.execute(order, courier)

        result shouldBe false
    }

    @Test
    fun `must return false if order status is not ASSIGNED`() {
        val courier = newFreeCourier()
        val order = newOrder()

        val result = sut.execute(order, courier)

        result shouldBe false
    }

    @Test
    fun `must return false if courier has not reached the order location`() {
        val courier = newBusyCourier(
            location = newLocation(1, 1)
        )
        val order = newAssignedOrder(
            location = newLocation(2, 2),
            courier = courier
        )

        val result = sut.execute(order, courier)

        result shouldBe false
    }

    @Test
    fun `must return true and complete order if all conditions are met`() {
        val sameLocation = newLocation(1, 1)
        val courier = newBusyCourier(
            location = sameLocation
        )
        val order = newAssignedOrder(
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
