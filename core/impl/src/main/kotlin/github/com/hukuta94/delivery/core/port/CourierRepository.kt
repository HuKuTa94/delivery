package github.com.hukuta94.delivery.core.port

import github.com.hukuta94.delivery.core.domain.courier.Courier
import java.util.*

interface CourierRepository {

    fun add(courier: Courier)

    fun update(courier: Courier)

    fun getById(id: UUID): Courier

    fun getAllFree(): List<Courier>
}