package github.com.hukuta94.delivery.configuration.infrastructure.grpc

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PreDestroy

@Configuration
open class GrpcConfiguration {

    private lateinit var geoServiceGrpChannel: ManagedChannel

    @Bean
    open fun geoServiceGrpChannelBean(): ManagedChannel {
        return ManagedChannelBuilder.forAddress("localhost", 8084)
            .usePlaintext() // disable SSL/TLS
            .build()
    }

    @PreDestroy
    fun geoServiceGrpChannelShutDown() {
        if (::geoServiceGrpChannel.isInitialized) {
            geoServiceGrpChannel.shutdown()
            println("gRPC channel shutdown")
        }
    }
}