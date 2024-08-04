package github.com.hukuta94.delivery.core.application.event.domain.handler

import github.com.hukuta94.delivery.core.application.event.ApplicationEventHandler
import github.com.hukuta94.delivery.core.domain.DomainEvent

interface DomainEventHandler<EVENT : DomainEvent> : ApplicationEventHandler<EVENT>