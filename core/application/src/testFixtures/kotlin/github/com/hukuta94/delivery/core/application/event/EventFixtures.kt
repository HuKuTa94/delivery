@file:Suppress(
    "MatchingDeclarationName",
    "Filename",
    "Reason: In real application may be more events"
)

package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.DomainEvent
import java.util.UUID
import kotlin.reflect.KClass

/**
 * Records the events it handles successfully.
 *
 * Every call, including a failing one, is appended to [invocationLog],
 * so a log shared by several handlers shows the order they were called in.
 */
class FakeEventHandler<EVENT : DomainEvent>(
    override val eventType: KClass<EVENT>,
    private val invocationLog: MutableList<EventHandler<*>> = mutableListOf(),
) : EventHandler<EVENT> {
    val handledEvents: List<EVENT>
        get() = received

    private val received = mutableListOf<EVENT>()
    private var failure: Exception? = null

    override fun handle(event: EVENT) {
        invocationLog += this
        failure?.let { throw it }
        received += event
    }

    fun failWith(exception: Exception) {
        failure = exception
    }
}

fun basketConfirmedIntegrationDomainEvent(
    basketId: UUID = UUID.randomUUID(),
    street: String = "Default street",
) = BasketConfirmedEvent(
    basketId = basketId,
    street = street,
)
