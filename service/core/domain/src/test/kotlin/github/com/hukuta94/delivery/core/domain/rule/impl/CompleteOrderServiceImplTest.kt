package github.com.hukuta94.delivery.core.domain.rule.impl

import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.busyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.core.domain.aggregate.order.assignedOrder
import github.com.hukuta94.delivery.core.domain.aggregate.order.createdOrder
import github.com.hukuta94.delivery.core.domain.common.location
import github.com.hukuta94.delivery.core.domain.rule.CompleteOrderBusinessRule
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class CompleteOrderServiceImplTest {

    // System Under Testing
    private val rule = CompleteOrderBusinessRuleImpl()

    @Test
    fun `must return business error if order is assigned to a different courier`() {
        val courier = freeCourier()
        val order = assignedOrder(courier = freeCourier())

        val result = rule.execute(order, courier)

        result shouldBeLeft CompleteOrderBusinessRule.Error.OrderAssignedToAnotherCourier
    }

    @Test
    fun `must return business error if order status is not ASSIGNED`() {
        val courier = freeCourier()
        val order = createdOrder()

        val result = rule.execute(order, courier)

        result shouldBeLeft CompleteOrderBusinessRule.Error.OrderIsNotAssigned
    }

    @Test
    fun `must return business error if courier has not reached the order location`() {
        val courier = busyCourier(
            location = location(1, 1)
        )
        val order = assignedOrder(
            location = location(2, 2),
            courier = courier
        )

        val result = rule.execute(order, courier)

        result shouldBeLeft CompleteOrderBusinessRule.Error.CourierNotReachedOrderLocation
    }

    @Test
    fun `must complete order if all conditions are met`() {
        val sameLocation = location(1, 1)
        val courier = busyCourier(
            location = sameLocation
        )
        val order = assignedOrder(
            courier = courier,
            location = sameLocation
        )

        rule.execute(order, courier).shouldBeRight()

        assertSoftly {
            courier.status shouldBe CourierStatus.FREE
            order.status shouldBe OrderStatus.COMPLETED
        }
    }
}
