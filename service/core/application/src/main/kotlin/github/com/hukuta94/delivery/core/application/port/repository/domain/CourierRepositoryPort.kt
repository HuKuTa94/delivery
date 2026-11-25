package github.com.hukuta94.delivery.core.application.port.repository.domain

import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import java.util.UUID

interface CourierRepositoryPort : AggregateRepositoryPort<Courier, UUID> {

    fun getAllFree(): Collection<Courier>

    fun getAllBusy(): Collection<Courier>
}
