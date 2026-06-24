package github.com.hukuta94.delivery.infrastructure.kafka

import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedDomainEvent
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedDomainEvent
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.apache.kafka.clients.producer.ProducerRecord
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import java.util.UUID
import java.util.concurrent.CompletableFuture

internal class OrderKafkaProducerTest : StringSpec({

    lateinit var kafkaTemplate: KafkaTemplate<UUID, ByteArray>
    lateinit var sut: OrderKafkaProducer

    beforeTest {
        kafkaTemplate = mock()
        whenever(kafkaTemplate.send(any<ProducerRecord<UUID, ByteArray>>()))
            .thenReturn(CompletableFuture.completedFuture(mock<SendResult<UUID, ByteArray>>()))
        sut = OrderKafkaProducer(kafkaTemplate)
    }

    "Order assignment is published with the order id" {
        val orderId = UUID.randomUUID()
        val event = OrderAssignedDomainEvent(orderId = orderId, courierId = UUID.randomUUID())

        sut.publishOrderAssignedDomainEvent(event)

        val record = captureSentRecord(kafkaTemplate)
        assertSoftly {
            record.key() shouldBe orderId
            record.key() shouldNotBe event.eventId
            val payload = OrderStatusChangedIntegrationEvent.parseFrom(record.value())
            payload.orderId shouldBe orderId.toString()
            payload.orderStatus shouldBe OrderStatus.Assigned
        }
    }

    "Order completion is published with the order id" {
        val orderId = UUID.randomUUID()
        val event = OrderCompletedDomainEvent(orderId = orderId)

        sut.publishOrderCompletedDomainEvent(event)

        val record = captureSentRecord(kafkaTemplate)
        assertSoftly {
            record.key() shouldBe orderId
            record.key() shouldNotBe event.eventId
            val payload = OrderStatusChangedIntegrationEvent.parseFrom(record.value())
            payload.orderId shouldBe orderId.toString()
            payload.orderStatus shouldBe OrderStatus.Completed
        }
    }

    "Publishing fails when the broker rejects the message" {
        whenever(kafkaTemplate.send(any<ProducerRecord<UUID, ByteArray>>()))
            .thenReturn(
                CompletableFuture<SendResult<UUID, ByteArray>>().apply {
                    completeExceptionally(RuntimeException("broker is down"))
                }
            )

        shouldThrow<IllegalStateException> {
            sut.publishOrderAssignedDomainEvent(
                OrderAssignedDomainEvent(orderId = UUID.randomUUID(), courierId = UUID.randomUUID())
            )
        }
    }

    "Publishing fails when delivery times out" {
        whenever(kafkaTemplate.send(any<ProducerRecord<UUID, ByteArray>>()))
            .thenReturn(
                CompletableFuture<SendResult<UUID, ByteArray>>().apply {
                    completeExceptionally(
                        org.apache.kafka.common.errors.TimeoutException("delivery timeout")
                    )
                }
            )

        shouldThrow<IllegalStateException> {
            sut.publishOrderCompletedDomainEvent(
                OrderCompletedDomainEvent(orderId = UUID.randomUUID())
            )
        }
    }
})

private fun captureSentRecord(
    kafkaTemplate: KafkaTemplate<UUID, ByteArray>,
): ProducerRecord<UUID, ByteArray> {
    val captor = argumentCaptor<ProducerRecord<UUID, ByteArray>>()
    verify(kafkaTemplate).send(captor.capture())
    return captor.firstValue
}
