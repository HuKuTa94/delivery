package github.com.hukuta94.delivery.api.startup.configuration

import github.com.hukuta94.delivery.infrastructure.adapter.grpc.GeoServiceGrpcClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class PortConfiguration {
    @Bean
    open fun getLocationPort() = GeoServiceGrpcClient()
}