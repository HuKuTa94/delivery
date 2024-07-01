package github.com.hukuta94.delivery.core.domain.order

import github.com.hukuta94.delivery.core.domain.courier.newCourier
import github.com.hukuta94.delivery.core.domain.sharedkernel.Location
import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import org.junit.jupiter.api.Test
import java.util.*

internal class OrderTest {

    @Test
    fun `new order is created in correct status and state`() {
        val order = Order.create(
            id = UUID.randomUUID(),
            location = Location.random()
        )

        assertSoftly {
            order.status shouldBe OrderStatus.CREATED
            order.courierId shouldBe null
        }
    }

    @Test
    fun `order status is changed when courier is assigned`() {
        val order = newOrder()
        val courier = newCourier()

        order.assignCourier(courier)

        assertSoftly {
            order.status shouldBe OrderStatus.ASSIGNED
            order.courierId shouldBe courier.id
        }
    }

    @Test
    fun `order can not be reassigned to other courier`() {
        val assignedOrder = assignedOrder()
        val completedOrder = completedOrder()
        val otherCourier = newCourier()
        assignedOrder.assignCourier(otherCourier)

        completedOrder.assignCourier(otherCourier)

        assertSoftly {
            assignedOrder.courierId shouldNotBe otherCourier.id
            completedOrder.courierId shouldNotBe otherCourier.id
        }
    }

    @Test
    fun `order can be completed when courier is assigned`() {
        val order = assignedOrder()

        order.complete()

        order.status shouldBe OrderStatus.COMPLETED
    }

    @Test
    fun `order can not be completed when courier is not assigned`() {
        val order = newOrder()

        order.complete()

        order.status shouldNotBe OrderStatus.COMPLETED
    }
}






