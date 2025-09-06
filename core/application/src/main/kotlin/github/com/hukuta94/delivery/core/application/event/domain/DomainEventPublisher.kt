package github.com.hukuta94.delivery.core.application.event.domain

import github.com.hukuta94.delivery.core.application.event.ApplicationEventPublisher
import github.com.hukuta94.delivery.core.application.event.domain.handler.DomainEventHandler
import github.com.hukuta94.delivery.core.domain.DomainEvent

class DomainEventPublisher : ApplicationEventPublisher<DomainEvent, DomainEventHandler<DomainEvent>>()
