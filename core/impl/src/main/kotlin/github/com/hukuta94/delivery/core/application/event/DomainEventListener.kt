package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.DomainEvent
import kotlin.reflect.KClass

interface DomainEventListener<T : DomainEvent> {

    val eventType: KClass<T>

    fun handle(domainEvent: T)
}