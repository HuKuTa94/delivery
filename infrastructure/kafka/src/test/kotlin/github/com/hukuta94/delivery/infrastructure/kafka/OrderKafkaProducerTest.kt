package github.com.hukuta94.delivery.infrastructure.kafka

import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderAssignedDomainEvent
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderCompletedDomainEvent
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.apache.kafka.clients.producer.ProducerRecord
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.kafka.core.KafkaTemplate
import java.util.UUID

internal class OrderKafkaProducerTest : StringSpec({

    lateinit var kafkaTemplate: KafkaTemplate<UUID, ByteArray>
    lateinit var sut: OrderKafkaProducer

    beforeTest {
        kafkaTemplate = mock()
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
})

private fun captureSentRecord(
    kafkaTemplate: KafkaTemplate<UUID, ByteArray>,
): ProducerRecord<UUID, ByteArray> {
    val captor = argumentCaptor<ProducerRecord<UUID, ByteArray>>()
    verify(kafkaTemplate).send(captor.capture())
    return captor.firstValue
}
