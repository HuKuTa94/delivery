package github.com.hukuta94.delivery.core.application.port.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus

class CourierRepositoryFake : AggregateRepositoryFake<Courier>(), CourierRepositoryPort {

    override fun getAllFree(): Collection<Courier> {
        return storage.values.filter { courier ->
            courier.status == CourierStatus.FREE
        }
    }

    override fun getAllBusy(): Collection<Courier> {
        return storage.values.filter { courier ->
            courier.status == CourierStatus.BUSY
        }
    }
}