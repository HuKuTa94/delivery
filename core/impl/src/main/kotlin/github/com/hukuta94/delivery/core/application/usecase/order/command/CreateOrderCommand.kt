package github.com.hukuta94.delivery.core.application.usecase.order.command

import java.util.*

interface CreateOrderCommand {
    fun execute(command: Command)
}

data class Command(
    val basketId: UUID,
    val street: String,
)