package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.DomainEvent
import kotlin.reflect.KClass

interface ApplicationEventHandler<EVENT : DomainEvent> {

    val eventType: KClass<EVENT>

    fun handle(event: EVENT)
}
