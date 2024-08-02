package github.com.hukuta94.delivery.core.domain.service

import github.com.hukuta94.delivery.core.domain.courier.Courier
import github.com.hukuta94.delivery.core.domain.order.Order

interface CompleteOrderService {

    //TODO Возвращает Boolean, чтобы было понятно удалось завершить заказ или нет. Лучше переделать на Arrow.kt
    fun execute(order: Order, courier: Courier): Boolean
}