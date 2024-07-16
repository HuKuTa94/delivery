package github.com.hukuta94.delivery.api.adapter.kafka

import github.com.hukuta94.delivery.core.application.usecase.order.command.Command
import github.com.hukuta94.delivery.core.application.usecase.order.command.CreateOrderCommand
import org.apache.kafka.clients.consumer.ConsumerRecord
import java.util.*

//TODO Почему спринг в private val createOrderCommand передает null, хотя бин создан?
class BasketConfirmedIntegrationEventKafkaConsumer(
    bootstrapServersConfig: String,
    groupIdConfig: String,
    topic: String,
    private val createOrderCommand: CreateOrderCommand,
) : IntegrationEventKafkaConsumer(
    bootstrapServersConfig,
    groupIdConfig,
    topic,
) {
    override fun handleMessage(message: ConsumerRecord<UUID, String>) {
        val basketConfirmedEventBuilder = BasketConfirmedIntegrationEvent.newBuilder()

        jsonParser.merge(message.value(), basketConfirmedEventBuilder)

        val basketConfirmedEvent = basketConfirmedEventBuilder.build()

        val command = Command(
            basketId = UUID.fromString(basketConfirmedEvent.basketId),
            street = basketConfirmedEvent.address.street,
        )

        createOrderCommand.execute(command)
    }
}