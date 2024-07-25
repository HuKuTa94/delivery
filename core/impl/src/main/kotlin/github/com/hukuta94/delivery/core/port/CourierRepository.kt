package github.com.hukuta94.delivery.core.port

import github.com.hukuta94.delivery.core.domain.courier.Courier

interface CourierRepository : Repository<Courier> {

    fun getAllFree(): Collection<Courier>

    fun getAllBusy(): Collection<Courier>
}