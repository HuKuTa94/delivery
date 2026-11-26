package github.com.hukuta94.delivery.bootstrap.infrastructure.grpc

import github.com.hukuta94.delivery.core.application.port.GetLocationPort
import github.com.hukuta94.delivery.infrastructure.grpc.GeoServiceGrpcClient
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    prefix = "delivery",
    name = ["geo-location"],
    havingValue = "grpc",
)
open class GrpcConfiguration {

    @Bean(destroyMethod = "shutdown")
    open fun geoServiceGrpChannel(
        @Value("\${delivery.grpc.geo.address:localhost}") address: String,
        @Value("\${delivery.grpc.geo.port:8084}") port: Int,
    ) = ManagedChannelBuilder
            .forAddress(address, port)
            .usePlaintext() // disable SSL/TLS
            .build()

    @Bean
    open fun grpcGetLocationPort(grpcChannel: ManagedChannel): GetLocationPort = GeoServiceGrpcClient(grpcChannel)
}
