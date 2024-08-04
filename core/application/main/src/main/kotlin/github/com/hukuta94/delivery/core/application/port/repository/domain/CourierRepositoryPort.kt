package github.com.hukuta94.delivery.core.application.port.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier

interface CourierRepositoryPort : AggregateRepository<Courier> {

    fun getAllFree(): Collection<Courier>

    fun getAllBusy(): Collection<Courier>
}