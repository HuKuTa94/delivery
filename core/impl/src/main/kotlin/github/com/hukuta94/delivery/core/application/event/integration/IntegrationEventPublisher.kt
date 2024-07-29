package github.com.hukuta94.delivery.core.application.event.integration

import github.com.hukuta94.delivery.core.application.event.ApplicationEventPublisher
import github.com.hukuta94.delivery.core.application.event.integration.handler.IntegrationEventHandler
import github.com.hukuta94.delivery.core.domain.IntegrationEvent

class IntegrationEventPublisher : ApplicationEventPublisher<IntegrationEvent, IntegrationEventHandler<IntegrationEvent>>()
