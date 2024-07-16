package github.com.hukuta94.delivery.api.adapter.kafka

import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderCommand
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase
import org.apache.kafka.clients.consumer.ConsumerRecord
import java.util.*

class BasketConfirmedIntegrationEventKafkaConsumer(
    bootstrapServersConfig: String,
    groupIdConfig: String,
    topic: String,
    private val createOrderUseCase: CreateOrderUseCase,
) : IntegrationEventKafkaConsumer(
    bootstrapServersConfig,
    groupIdConfig,
    topic,
) {
    override fun handleMessage(message: ConsumerRecord<UUID, String>) {
        val basketConfirmedEventBuilder = BasketConfirmedIntegrationEvent.newBuilder()

        jsonParser.merge(message.value(), basketConfirmedEventBuilder)

        val basketConfirmedEvent = basketConfirmedEventBuilder.build()

        val command = CreateOrderCommand(
            basketId = UUID.fromString(basketConfirmedEvent.basketId),
            street = basketConfirmedEvent.address.street,
        )

        createOrderUseCase.execute(command)
    }
}