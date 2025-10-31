package github.com.hukuta94.delivery.core.domain.rule

import arrow.core.Either
import github.com.hukuta94.delivery.core.domain.BusinessError
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.order.Order

interface CompleteOrderBusinessRule : BusinessRule {

    fun execute(
        order: Order,
        courier: Courier,
    ): Either<Error, Unit>

    sealed class Error(
        override val message: String,
    ) : BusinessError {
        data object OrderIsNotAssigned : Error("The order is not assigned")
        data object OrderAssignedToAnotherCourier : Error("The order assigned to another courier courier")
        data object CourierNotReachedOrderLocation : Error("The courier not reached order location")
    }
}