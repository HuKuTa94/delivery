package github.com.hukuta94.delivery.api.startup.configuration.application

import github.com.hukuta94.delivery.api.startup.configuration.adapter.grpc.GrpcConfiguration
import github.com.hukuta94.delivery.infrastructure.adapter.grpc.GeoServiceGrpcClient
import io.grpc.ManagedChannel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    GrpcConfiguration::class,
)
open class PortConfiguration {

    @Bean
    open fun getLocationPort(
        grpcChannel: ManagedChannel,
    ) = GeoServiceGrpcClient(grpcChannel)
}