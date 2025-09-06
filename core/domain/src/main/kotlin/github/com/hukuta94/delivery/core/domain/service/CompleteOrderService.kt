package github.com.hukuta94.delivery.core.domain.service

import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.order.Order

interface CompleteOrderService : DomainService {

    //TODO Возвращает Boolean, чтобы было понятно удалось завершить заказ или нет. Лучше переделать на Arrow.kt
    fun execute(order: Order, courier: Courier): Boolean
}