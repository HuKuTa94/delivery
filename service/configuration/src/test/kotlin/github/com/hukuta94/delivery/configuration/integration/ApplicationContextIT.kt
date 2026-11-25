package github.com.hukuta94.delivery.configuration.integration

import github.com.hukuta94.delivery.configuration.core.CoreConfiguration
import github.com.hukuta94.delivery.configuration.infrastructure.InfrastructureConfiguration
import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.OrderRepositoryPort
import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.application.query.order.GetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.infrastructure.orm.commons.integration.PostgresTestContainer
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.MapPropertySource

@Configuration
@EnableAutoConfiguration
@Import(CoreConfiguration::class, InfrastructureConfiguration::class)
open class ApplicationContextTestConfig

/**
 * Verifies A1/A8: under each `delivery.orm` profile the context starts and wires exactly one bean
 * per persistence port and read query (no NoUniqueBean / BeanDefinitionOverride). Requires Docker.
 */
internal class ApplicationContextIT : StringSpec({

    fun buildContext(orm: String): ConfigurableApplicationContext =
        SpringApplicationBuilder(ApplicationContextTestConfig::class.java)
            .web(WebApplicationType.NONE)
            .initializers(
                ApplicationContextInitializer<ConfigurableApplicationContext> { ctx ->
                    // addFirst -> highest precedence, overrides application.yml datasource
                    ctx.environment.propertySources.addFirst(
                        MapPropertySource(
                            "integration-test",
                            mapOf<String, Any>(
                                "delivery.orm" to orm,
                                "spring.datasource.url" to PostgresTestContainer.jdbcUrl,
                                "spring.datasource.username" to PostgresTestContainer.username,
                                "spring.datasource.password" to PostgresTestContainer.password,
                                "spring.jpa.hibernate.ddl-auto" to "none",
                                "spring.liquibase.enabled" to "false",
                            ),
                        ),
                    )
                },
            )
            .run()

    fun ConfigurableApplicationContext.assertSingleBeanPerType() {
        assertSoftly {
            getBeanNamesForType(UnitOfWorkPort::class.java).size shouldBe 1

            // Order
            getBeanNamesForType(OrderRepositoryPort::class.java).size shouldBe 1
            getBeanNamesForType(GetNotCompletedOrdersQuery::class.java).size shouldBe 1

            // Courier
            getBeanNamesForType(GetBusyCouriersQuery::class.java).size shouldBe 1
            getBeanNamesForType(CourierRepositoryPort::class.java).size shouldBe 1
        }
    }

    "spring-jpa profile starts and wires exactly one bean per port/query" {
        val context = buildContext("spring-jpa")
        try {
            context.assertSingleBeanPerType()
        } finally {
            context.close()
        }
    }

    "ktorm profile starts and wires exactly one bean per port/query" {
        val context = buildContext("ktorm")
        try {
            context.assertSingleBeanPerType()
        } finally {
            context.close()
        }
    }
})
