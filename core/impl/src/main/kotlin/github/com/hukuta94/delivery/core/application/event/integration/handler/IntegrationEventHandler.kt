package github.com.hukuta94.delivery.core.application.event.integration.handler

import github.com.hukuta94.delivery.core.application.event.ApplicationEventHandler
import github.com.hukuta94.delivery.core.application.event.integration.IntegrationEvent

interface IntegrationEventHandler<EVENT : IntegrationEvent> : ApplicationEventHandler<EVENT>