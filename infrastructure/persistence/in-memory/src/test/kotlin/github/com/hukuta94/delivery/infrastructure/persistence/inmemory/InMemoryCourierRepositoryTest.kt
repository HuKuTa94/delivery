package github.com.hukuta94.delivery.infrastructure.persistence.inmemory

import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.busyCourier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import java.util.UUID

internal class InMemoryCourierRepositoryTest : StringSpec({

    // System Under Testing (sut)
    lateinit var repository: InMemoryCourierRepository

    beforeTest {
        repository = InMemoryCourierRepository()
    }

    "can add the courier" {
        // Given
        val courier = freeCourier()
        repository.add(courier)

        // When
        val actual = repository.getById(courier.id)

        // Then
        actual shouldBeSameInstanceAs courier
    }

    "can update the courier" {
        // Given
        val courierId = UUID.randomUUID()
        val courierInStorage = freeCourier(
            id = courierId,
        )
        val courierToUpdate = busyCourier(
            id = courierId,
            name = courierInStorage.name,
            location = courierInStorage.location,
            transport = courierInStorage.transport,
        )
        repository.add(courierInStorage)

        // When
        repository.update(courierToUpdate)
        val actual = repository.getById(courierId)

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
            repository.add(freeCourier())
        }
        repository.add(busyCourier())

        // When
        val actual = repository.getAllFree()

        // Then
        actual.size shouldBe countOfFreeCouriers
    }
})
