package github.com.hukuta94.delivery.core.application.usecase.courier.query.impl

import github.com.hukuta94.delivery.core.application.usecase.courier.query.GetFreeCouriersQuery
import github.com.hukuta94.delivery.core.application.usecase.courier.query.GetFreeCourierResponse
import github.com.hukuta94.delivery.core.port.CourierRepository

//TODO Query use case должен получать необходимые данные в обход полного восстановления агрегата.
class GetFreeCouriersQueryImpl(
    private val courierRepository: CourierRepository
) : GetFreeCouriersQuery {
    override fun execute(): List<GetFreeCourierResponse> {
        return courierRepository.getAllFree().map { courier ->
            GetFreeCourierResponse(
                id = courier.id,
                name = courier.name.value,
                locationAbscissa = courier.location.abscissa,
                locationOrdinate = courier.location.ordinate,
            )
        }
    }
}