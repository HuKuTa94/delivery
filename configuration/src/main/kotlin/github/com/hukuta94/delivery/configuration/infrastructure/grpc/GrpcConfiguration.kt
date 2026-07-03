package github.com.hukuta94.delivery.configuration.infrastructure.grpc

import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class GrpcConfiguration {

    @Bean(destroyMethod = "shutdown")
    open fun geoServiceGrpChannel(
        @Value("\${delivery.grpc.geo.address:localhost}") address: String,
        @Value("\${delivery.grpc.geo.port:8084}") port: Int,
    ) = ManagedChannelBuilder
            .forAddress(address, port)
            .usePlaintext() // disable SSL/TLS
            .build()
}
