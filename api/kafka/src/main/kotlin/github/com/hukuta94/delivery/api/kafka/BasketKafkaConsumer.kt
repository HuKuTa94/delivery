package github.com.hukuta94.delivery.api.kafka

import com.google.protobuf.util.JsonFormat
import github.com.hukuta94.delivery.core.application.event.BasketConfirmedIntegrationDomainEvent
import github.com.hukuta94.delivery.core.application.usecase.event.SaveIntegrationEventUseCase
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import java.util.*

class BasketKafkaConsumer(
    private val saveIntegrationEventUseCase: SaveIntegrationEventUseCase,
) {
    @KafkaListener(
        groupId = "basket-consumer-group",
        topics = [ "basket.confirmed" ],
    )
    fun basketConfirmedTopic(message: ConsumerRecord<String, String>) {
        LOG.info("Begin handling message with key: ${message.key()}")

        val basketConfirmedEventBuilder = BasketConfirmedIntegrationEvent.newBuilder()

        JSON_PARSER.merge(message.value(), basketConfirmedEventBuilder)

        val basketConfirmedEvent = basketConfirmedEventBuilder.build()

        val integrationEvent = BasketConfirmedIntegrationDomainEvent(
            basketId = UUID.fromString(basketConfirmedEvent.basketId),
            street = basketConfirmedEvent.address.street,
        )

        saveIntegrationEventUseCase.execute(integrationEvent)

        LOG.info("Finish handling message with key: ${message.key()}")
    }

    companion object {
        private val JSON_PARSER = JsonFormat.parser()
        private val LOG = LoggerFactory.getLogger(BasketKafkaConsumer::class.java)
    }
}
