package github.com.hukuta94.delivery.core.application.event.integration

import kotlin.reflect.KClass

interface IntegrationEventHandler<T : IntegrationEvent> {

    val eventType: KClass<T>

    fun handle(integrationEvent: T)
}