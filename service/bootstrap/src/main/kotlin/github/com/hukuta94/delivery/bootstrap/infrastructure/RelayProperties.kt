package github.com.hukuta94.delivery.bootstrap.infrastructure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "delivery.relay")
data class RelayProperties(
    val batchSize: Int = 50,
)
