package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.FakeDomainEvent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ApplicationEventPublisherTest : StringSpec({

    "must publish event if it has registered handler" {
        // Given
        val event = FakeDomainEvent()
        val eventHandler = FakeEventHandler()
        val sut = ApplicationEventPublisher(listOf(eventHandler))

        // When
        sut.publish(event)

        // Then
        eventHandler.executedCount shouldBe 1
    }
})