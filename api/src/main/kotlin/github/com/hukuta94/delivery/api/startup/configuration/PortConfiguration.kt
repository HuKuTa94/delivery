package github.com.hukuta94.delivery.api.startup.configuration

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

}