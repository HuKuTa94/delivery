package github.com.hukuta94.delivery.infrastructure.adapter.inmemory

import github.com.hukuta94.delivery.core.domain.courier.newCourier
import github.com.hukuta94.delivery.core.domain.order.assignedOrder
import github.com.hukuta94.delivery.core.domain.order.newOrder
import io.kotlintest.matchers.types.shouldBeSameInstanceAs
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class OrderInMemoryRepositoryTest {

    // System Under Testing (sut)
    private lateinit var sut: OrderInMemoryRepository

    @BeforeEach
    fun init() {
        sut = OrderInMemoryRepository()
    }

    @Test
    fun `can add the order`() {
        // given
        val order = newOrder()

        // when
        sut.add(order)

        // then
        sut.getById(order.id) shouldBeSameInstanceAs order
    }

    @Test
    fun `can update the order`() {
        // given
        val order = newOrder()
        sut.add(order)
        val orderFromRepository = sut.getById(order.id)
        orderFromRepository.courierId shouldBe null
        orderFromRepository.assignCourier(newCourier())

        // when
        sut.update(orderFromRepository)

        // then
        sut.getById(orderFromRepository.id).courierId shouldNotBe null
    }

    @Test
    fun `can get all created orders`() {
        // given
        val countOfCreatedOrders = 3
        repeat(countOfCreatedOrders) {
            sut.add(newOrder())
        }
        val assignedOrder = assignedOrder()
        sut.add(assignedOrder)

        // when
        val result = sut.getAllCreated()

        // then
        result.size shouldBe countOfCreatedOrders
    }

    @Test
    fun `can get all assigned orders`() {
        // given
        val countOfAssignedOrders = 3
        repeat(countOfAssignedOrders) {
            sut.add(assignedOrder())
        }
        val createdOrder = newOrder()
        sut.add(createdOrder)

        // when
        val result = sut.getAllAssigned()

        // then
        result.size shouldBe countOfAssignedOrders
    }
}