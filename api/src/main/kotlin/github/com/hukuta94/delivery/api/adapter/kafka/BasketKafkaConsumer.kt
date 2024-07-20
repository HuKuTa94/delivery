package github.com.hukuta94.delivery.api.adapter.kafka

import com.google.protobuf.util.JsonFormat
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderCommand
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import java.util.*

class BasketKafkaConsumer(
    private val createOrderUseCase: CreateOrderUseCase,
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

        val command = CreateOrderCommand(
            basketId = UUID.fromString(basketConfirmedEvent.basketId),
            street = basketConfirmedEvent.address.street,
        )

        createOrderUseCase.execute(command)

        LOG.info("Finish handling message with key: ${message.key()}")
    }

    companion object {
        private val JSON_PARSER = JsonFormat.parser()
        private val LOG = LoggerFactory.getLogger(BasketKafkaConsumer::class.java)
    }
}