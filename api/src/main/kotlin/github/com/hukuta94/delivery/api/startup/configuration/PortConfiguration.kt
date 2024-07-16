package github.com.hukuta94.delivery.api.startup.configuration

import github.com.hukuta94.delivery.api.adapter.kafka.BasketConfirmedIntegrationEventKafkaConsumer
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase
import github.com.hukuta94.delivery.infrastructure.adapter.grpc.GeoServiceGrpcClient
import github.com.hukuta94.delivery.infrastructure.adapter.kafka.OrderIntegrationEventKafkaProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PortConfiguration {
    @Bean
    open fun getLocationPort() = GeoServiceGrpcClient()

    //TODO Необходимо подумать над закрытием ресурса
    @Bean
    open fun busProducer() = OrderIntegrationEventKafkaProducer(
        bootstrapServersConfig = "localhost:9092",
    )

    //TODO 1. Необходимо подумать над закрытием ресурса
    // 2. Почему Spring при создании BasketConfirmedIntegrationEventKafkaConsumer
    // передает null createOrderUseCase, хотя его бин создан?
    // Выяснилось, что в родительском абстрактном классе из init блока запускается бесконечный цикл
    // из-за этог оне происходит инжект юзкейса в наследнике
    @Bean
    open fun busConsumer(
        createOrderUseCase: CreateOrderUseCase,
    ): BasketConfirmedIntegrationEventKafkaConsumer {
        return BasketConfirmedIntegrationEventKafkaConsumer(
            bootstrapServersConfig = "localhost:9092",
            groupIdConfig = "basket-consumer-group",
            topic = "basket.confirmed",
            createOrderUseCase = createOrderUseCase,
        )
    }
}