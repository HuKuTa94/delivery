package github.com.hukuta94.delivery.bootstrap.infrastructure.persistence.inmemory

import github.com.hukuta94.delivery.core.application.port.GetLocationPort
import github.com.hukuta94.delivery.infrastructure.persistence.inmemory.InMemoryGetLocation
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(
    prefix = "delivery",
    name = ["geo-location"],
    havingValue = "in-memory",
)
open class InMemoryGeoLocationConfiguration {
    @Bean
    open fun inMemoryGetLocationPort(): GetLocationPort = InMemoryGetLocation()
}
