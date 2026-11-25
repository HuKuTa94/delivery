package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.AnotherFakeDomainEvent
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.core.domain.FakeDomainEvent
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.types.shouldBeSameInstanceAs

class EventPublisherTest : StringSpec({

    "event is delivered to the handler registered for its type" {
        // Given
        val event = FakeDomainEvent()
        val handler = FakeEventHandler(FakeDomainEvent::class)
        val sut = EventPublisher(listOf(handler))

        // When
        sut.publish(event)

        // Then
        handler.handledEvents shouldContainExactly listOf(event)
    }

    "event is not delivered to handlers of other event types" {
        // Given
        val event = AnotherFakeDomainEvent()
        val fakeEventHandler = FakeEventHandler(FakeDomainEvent::class)
        val anotherFakeEventHandler = FakeEventHandler(AnotherFakeDomainEvent::class)
        val sut = EventPublisher(listOf(fakeEventHandler, anotherFakeEventHandler))

        // When
        sut.publish(event)

        // Then
        assertSoftly {
            anotherFakeEventHandler.handledEvents shouldContainExactly listOf(event)
            fakeEventHandler.handledEvents.shouldBeEmpty()
        }
    }

    "event without a registered handler is skipped without error" {
        // Given
        val sut = EventPublisher(emptyList())

        // When
        val result = runCatching { sut.publish(FakeDomainEvent()) }

        // Then
        result.shouldBeSuccess()
    }

    "every handler registered for the event type receives the event once in registration order" {
        // Given
        val event = FakeDomainEvent()
        val invocationLog = mutableListOf<EventHandler<*>>()
        val first = FakeEventHandler(
            eventType = FakeDomainEvent::class,
            invocationLog = invocationLog,
        )
        val second = FakeEventHandler(
            eventType = FakeDomainEvent::class,
            invocationLog = invocationLog,
        )
        val third = FakeEventHandler(
            eventType = FakeDomainEvent::class,
            invocationLog = invocationLog,
        )
        val sut = EventPublisher(listOf(first, second, third))

        // When
        sut.publish(event)

        // Then
        assertSoftly {
            invocationLog shouldContainExactly listOf(first, second, third)
            first.handledEvents shouldContainExactly listOf(event)
            second.handledEvents shouldContainExactly listOf(event)
            third.handledEvents shouldContainExactly listOf(event)
        }
    }

    "handler registered for a parent event type does not receive its subtypes" {
        // Given
        val parentTypeHandler = FakeEventHandler(DomainEvent::class)
        val sut = EventPublisher(listOf(parentTypeHandler))

        // When
        sut.publish(FakeDomainEvent())

        // Then
        parentTypeHandler.handledEvents.shouldBeEmpty()
    }

    "handler failure reaches the caller unchanged" {
        // Given
        val failure = IllegalStateException("handler failed")
        val handler = FakeEventHandler(FakeDomainEvent::class).apply { failWith(failure) }
        val sut = EventPublisher(listOf(handler))

        // When
        val exception = shouldThrow<IllegalStateException> { sut.publish(FakeDomainEvent()) }

        // Then
        exception shouldBeSameInstanceAs failure
    }

    "handlers registered after a failed one still receive the event" {
        // Given
        val event = FakeDomainEvent()
        val failingHandler = FakeEventHandler(FakeDomainEvent::class).apply {
            failWith(IllegalStateException("handler failed"))
        }
        val nextHandler = FakeEventHandler(FakeDomainEvent::class)
        val sut = EventPublisher(listOf(failingHandler, nextHandler))

        // When
        shouldThrow<IllegalStateException> { sut.publish(event) }

        // Then
        nextHandler.handledEvents shouldContainExactly listOf(event)
    }

    "when several handlers fail the caller gets the first failure with the others attached" {
        // Given
        val firstFailure = IllegalStateException("first handler failed")
        val secondFailure = IllegalArgumentException("second handler failed")
        val firstHandler = FakeEventHandler(FakeDomainEvent::class).apply { failWith(firstFailure) }
        val secondHandler = FakeEventHandler(FakeDomainEvent::class).apply { failWith(secondFailure) }
        val sut = EventPublisher(listOf(firstHandler, secondHandler))

        // When
        val exception = shouldThrow<IllegalStateException> { sut.publish(FakeDomainEvent()) }

        // Then
        assertSoftly {
            exception shouldBeSameInstanceAs firstFailure
            exception.suppressed.toList() shouldContainExactly listOf(secondFailure)
        }
    }

    "handlers failing with the same exception give the caller that exception" {
        // Given
        val sharedFailure = IllegalStateException("shared failure")
        val firstHandler = FakeEventHandler(FakeDomainEvent::class).apply { failWith(sharedFailure) }
        val secondHandler = FakeEventHandler(FakeDomainEvent::class).apply { failWith(sharedFailure) }
        val sut = EventPublisher(listOf(firstHandler, secondHandler))

        // When
        val exception = shouldThrow<IllegalStateException> { sut.publish(FakeDomainEvent()) }

        // Then
        assertSoftly {
            exception shouldBeSameInstanceAs sharedFailure
            exception.suppressed.shouldBeEmpty()
        }
    }

    "same event published twice is delivered twice" {
        // Given
        val event = FakeDomainEvent()
        val handler = FakeEventHandler(FakeDomainEvent::class)
        val sut = EventPublisher(listOf(handler))

        // When
        repeat(2) { sut.publish(event) }

        // Then
        handler.handledEvents shouldContainExactly listOf(event, event)
    }

    "handler added to the source list after the publisher is created does not receive events" {
        // Given
        val handlers = mutableListOf<EventHandler<out DomainEvent>>()
        val sut = EventPublisher(handlers)
        val lateHandler = FakeEventHandler(FakeDomainEvent::class)
        handlers += lateHandler

        // When
        sut.publish(FakeDomainEvent())

        // Then
        lateHandler.handledEvents.shouldBeEmpty()
    }
})
