package github.com.hukuta94.delivery.configuration.api.http

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "delivery.cors")
data class CorsProperties(
    val allowedOrigins: List<String> = listOf("http://localhost:8086"),
    val allowedMethods: List<String> = listOf("GET", "POST"),
)
