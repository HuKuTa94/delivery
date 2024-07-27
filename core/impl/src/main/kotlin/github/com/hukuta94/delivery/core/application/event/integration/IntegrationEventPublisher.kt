package github.com.hukuta94.delivery.core.application.event.integration

interface IntegrationEventPublisher {

    fun publish(integrationEvent: IntegrationEvent)
}