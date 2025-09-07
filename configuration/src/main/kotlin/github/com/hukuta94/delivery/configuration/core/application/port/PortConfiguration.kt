package github.com.hukuta94.delivery.configuration.core.application.port

import github.com.hukuta94.delivery.infrastructure.grpc.GeoServiceGrpcClient
import github.com.hukuta94.delivery.configuration.infrastructure.grpc.GrpcConfiguration
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