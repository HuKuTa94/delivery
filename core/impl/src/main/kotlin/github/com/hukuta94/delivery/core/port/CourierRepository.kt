package github.com.hukuta94.delivery.core.port

import github.com.hukuta94.delivery.core.domain.courier.Courier

abstract class CourierRepository : Repository<Courier>() {

    abstract fun getAllFree(): Collection<Courier>

    abstract fun getAllBusy(): Collection<Courier>
}