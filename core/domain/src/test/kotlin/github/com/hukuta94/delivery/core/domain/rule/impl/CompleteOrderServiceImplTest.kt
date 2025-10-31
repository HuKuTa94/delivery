package github.com.hukuta94.delivery.core.domain.rule.impl

import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.newBusyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.newFreeCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.newCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.newAssignedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.newOrder
import github.com.hukuta94.delivery.core.domain.common.newLocation
import github.com.hukuta94.delivery.core.domain.rule.CompleteOrderBusinessRule
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class CompleteOrderServiceImplTest {

    // System Under Testing
    private val sut = CompleteOrderBusinessRuleImpl()

    @Test
    fun `must return business error if order is assigned to a different courier`() {
        val courier = newFreeCourier()
        val order = newAssignedOrder(courier = newCourier())

        val result = sut.execute(order, courier)

        result shouldBeLeft CompleteOrderBusinessRule.Error.OrderAssignedToAnotherCourier
    }

    @Test
    fun `must return business error if order status is not ASSIGNED`() {
        val courier = newFreeCourier()
        val order = newOrder()

        val result = sut.execute(order, courier)

        result shouldBeLeft CompleteOrderBusinessRule.Error.OrderIsNotAssigned
    }

    @Test
    fun `must return business error if courier has not reached the order location`() {
        val courier = newBusyCourier(
            location = newLocation(1, 1)
        )
        val order = newAssignedOrder(
            location = newLocation(2, 2),
            courier = courier
        )

        val result = sut.execute(order, courier)

        result shouldBeLeft CompleteOrderBusinessRule.Error.CourierNotReachedOrderLocation
    }

    @Test
    fun `must complete order if all conditions are met`() {
        val sameLocation = newLocation(1, 1)
        val courier = newBusyCourier(
            location = sameLocation
        )
        val order = newAssignedOrder(
            courier = courier,
            location = sameLocation
        )

        sut.execute(order, courier).shouldBeRight()

        assertSoftly {
            courier.status shouldBe CourierStatus.FREE
            order.status shouldBe OrderStatus.COMPLETED
        }
    }
}
