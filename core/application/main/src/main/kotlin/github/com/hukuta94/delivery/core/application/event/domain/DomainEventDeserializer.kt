package github.com.hukuta94.delivery.core.application.event.domain

import github.com.hukuta94.delivery.core.application.event.ApplicationEventDeserializer
import github.com.hukuta94.delivery.core.domain.DomainEvent

class DomainEventDeserializer : ApplicationEventDeserializer<DomainEvent>()