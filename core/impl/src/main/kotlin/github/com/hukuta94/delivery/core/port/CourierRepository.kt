package github.com.hukuta94.delivery.core.port

import github.com.hukuta94.delivery.core.application.event.DomainEventPublisher
import github.com.hukuta94.delivery.core.domain.courier.Courier

abstract class CourierRepository(
    domainEventPublisher: DomainEventPublisher,
) : Repository<Courier>(domainEventPublisher) {

    abstract fun getAllFree(): Collection<Courier>

    abstract fun getAllBusy(): Collection<Courier>
}