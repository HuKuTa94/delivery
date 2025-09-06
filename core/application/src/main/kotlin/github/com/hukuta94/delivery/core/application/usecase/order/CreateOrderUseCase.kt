package github.com.hukuta94.delivery.core.application.usecase.order

import github.com.hukuta94.delivery.core.application.usecase.UseCase
import java.util.*

interface CreateOrderUseCase : UseCase {
    fun execute(command: CreateOrderCommand)
}

data class CreateOrderCommand(
    val basketId: UUID,
    val street: String,
)