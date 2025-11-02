package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.FakeDomainEvent

class FakeEventHandler: ApplicationEventHandler<FakeDomainEvent> {
    var executedCount: Int = 0
        private set

    override val eventType = FakeDomainEvent::class

    override fun handle(event: FakeDomainEvent) {
        executedCount++
    }
}
