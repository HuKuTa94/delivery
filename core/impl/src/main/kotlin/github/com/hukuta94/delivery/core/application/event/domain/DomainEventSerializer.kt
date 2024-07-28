package github.com.hukuta94.delivery.core.application.event.domain

import github.com.hukuta94.delivery.core.application.event.ApplicationEventSerializer
import github.com.hukuta94.delivery.core.domain.DomainEvent

class DomainEventSerializer : ApplicationEventSerializer<DomainEvent>() {

    override val eventPackage = "github.com.hukuta94.delivery.core.domain."
}