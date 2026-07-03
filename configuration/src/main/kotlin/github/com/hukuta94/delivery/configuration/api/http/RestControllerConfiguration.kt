package github.com.hukuta94.delivery.configuration.api.http

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@EnableWebMvc
@Configuration
@EnableConfigurationProperties(
    CorsProperties::class,
)
@Import(
    OrderRestConfiguration::class,
    CourierRestConfiguration::class,
)
open class RestControllerConfiguration {
    @Bean
    open fun corsFilter(corsProperties: CorsProperties): CorsFilter {
        val config = CorsConfiguration().apply {
            allowCredentials = true
            allowedOrigins = corsProperties.allowedOrigins
            addAllowedHeader("*")
            allowedMethods = corsProperties.allowedMethods
        }
        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }

        return CorsFilter(source)
    }
}
