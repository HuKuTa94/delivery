package github.com.hukuta94.delivery.core.application.usecase.order

import java.util.*

interface CreateOrderUseCase {
    fun execute(command: CreateOrderCommand)
}

data class CreateOrderCommand(
    val basketId: UUID,
    val street: String,
)