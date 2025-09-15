package github.com.hukuta94.delivery.infrastructure.inmemory

import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.newBusyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.newCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.newFreeCourier
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import java.util.UUID

internal class CourierInMemoryRepositoryTest : StringSpec({

    // System Under Testing (sut)
    lateinit var sut: CourierInMemoryRepository

    beforeTest {
        sut = CourierInMemoryRepository()
    }

    "can add the courier" {
        // Given
        val courier = newCourier()
        sut.add(courier)

        // When
        val actual = sut.getById(courier.id)

        // Then
        actual shouldBeSameInstanceAs courier
    }

    "can update the courier" {
        // Given
        val courierId = UUID.randomUUID()
        val courierInStorage = newFreeCourier(
            id = courierId,
        )
        val courierToUpdate = newBusyCourier(
            id = courierId,
            name = courierInStorage.name,
            location = courierInStorage.location,
            transport = courierInStorage.transport,
        )
        sut.add(courierInStorage)

        // When
        sut.update(courierToUpdate)
        val actual = sut.getById(courierId)

        // Then
        assertSoftly {
            actual shouldNotBeSameInstanceAs courierInStorage
            actual.id shouldBe courierId
            actual.name shouldBe courierInStorage.name
            actual.location shouldBe courierInStorage.location
            actual.transport shouldBe courierInStorage.transport
            actual.status shouldNotBe courierInStorage.status
            actual.status shouldBe CourierStatus.BUSY
        }
    }

    "can get all free couriers" {
        // Given
        val countOfFreeCouriers = 3
        repeat(countOfFreeCouriers) {
            sut.add(newFreeCourier())
        }
        sut.add(newBusyCourier())

        // When
        val actual = sut.getAllFree()

        // Then
        actual.size shouldBe countOfFreeCouriers
    }
})
