package github.com.hukuta94.delivery.core.application.event

import kotlin.reflect.KClass

interface ApplicationEventHandler<EVENT : Any> {

    val eventType: KClass<EVENT>

    fun handle(event: EVENT)
}