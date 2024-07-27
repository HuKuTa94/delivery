package github.com.hukuta94.delivery.api.adapter.kafka

import com.google.protobuf.util.JsonFormat
import github.com.hukuta94.delivery.core.port.repository.box.InboxRepository
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import java.util.*

class BasketKafkaConsumer(
    private val inboxRepository: InboxRepository,
) {
    @KafkaListener(
        groupId = "basket-consumer-group",
        topics = [ "basket.confirmed" ],
    )
    fun basketConfirmedTopic(message: ConsumerRecord<UUID, String>) {
        LOG.info("Begin handling message with key: ${message.key()}")

        val basketConfirmedEventBuilder = BasketConfirmedIntegrationEvent.newBuilder()

        JSON_PARSER.merge(message.value(), basketConfirmedEventBuilder)

        val basketConfirmedEvent = basketConfirmedEventBuilder.build()

        val integrationEvent = github.com.hukuta94.delivery.core.application.event.integration.BasketConfirmedIntegrationEvent(
            id = UUID.randomUUID(), //TODO ID должен проставлятьcz из полученного интеграционного события
            basketId = UUID.fromString(basketConfirmedEvent.basketId),
            street = basketConfirmedEvent.address.street,
        )

        inboxRepository.saveIntegrationEvent(integrationEvent)

        LOG.info("Finish handling message with key: ${message.key()}")
    }

    companion object {
        private val JSON_PARSER = JsonFormat.parser()
        private val LOG = LoggerFactory.getLogger(BasketKafkaConsumer::class.java)
    }
}