package github.com.hukuta94.delivery.core.application.usecase.courier.impl

import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCourierResponse
import github.com.hukuta94.delivery.core.port.CourierRepository

//TODO Query use case должен получать необходимые данные в обход полного восстановления агрегата.
class GetBusyCouriersQueryImpl(
    private val courierRepository: CourierRepository,
) : GetBusyCouriersQuery {
    override fun execute(): List<GetBusyCourierResponse> {
        return courierRepository.getAllBusy().map { courier ->
            GetBusyCourierResponse(
                id = courier.id,
                name = courier.name.value,
                locationAbscissa = courier.location.abscissa,
                locationOrdinate = courier.location.ordinate,
            )
        }
    }
}