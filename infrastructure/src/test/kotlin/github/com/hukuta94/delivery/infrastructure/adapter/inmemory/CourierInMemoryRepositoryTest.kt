package github.com.hukuta94.delivery.infrastructure.adapter.inmemory

import github.com.hukuta94.delivery.core.domain.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.courier.newCourier
import io.kotlintest.matchers.types.shouldBeSameInstanceAs
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CourierInMemoryRepositoryTest {

    // System Under Testing (sut)
    private lateinit var sut: CourierInMemoryRepository

    @BeforeEach
    fun init() {
        sut = CourierInMemoryRepository()
    }

    @Test
    fun `can add the courier`() {
        // given
        val courier = newCourier()

        // when
        sut.add(courier)

        // then
        sut.getById(courier.id) shouldBeSameInstanceAs courier
    }

    @Test
    fun `can update the courier`() {
        // given
        val courier = newCourier()
        sut.add(courier)
        val courierFromRepository = sut.getById(courier.id)
        courierFromRepository.status shouldNotBe CourierStatus.BUSY
        courierFromRepository.busy()

        // when
        sut.update(courierFromRepository)

        // then
        sut.getById(courierFromRepository.id).status shouldBe CourierStatus.BUSY
    }

    @Test
    fun `can get all free couriers`() {
        // given
        val countOfFreeCouriers = 3
        repeat(countOfFreeCouriers) {
            sut.add(newCourier())
        }
        val busyCourier = newCourier().apply { busy() }
        sut.add(busyCourier)

        // when
        val result = sut.getAllFree()

        // then
        result.size shouldBe countOfFreeCouriers
    }
}