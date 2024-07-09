package github.com.hukuta94.delivery.core.port

import github.com.hukuta94.delivery.core.domain.courier.Courier
import java.util.*

interface CourierRepository {

    fun add(courier: Courier)

    fun update(courier: Courier)

    fun update(couriers: List<Courier>)

    fun getById(id: UUID): Courier

    fun getAllFree(): List<Courier>

    fun getAllBusy(): List<Courier>
}