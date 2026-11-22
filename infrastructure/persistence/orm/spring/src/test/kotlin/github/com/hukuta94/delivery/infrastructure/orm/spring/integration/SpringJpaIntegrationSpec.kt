package github.com.hukuta94.delivery.infrastructure.orm.spring.integration

import github.com.hukuta94.delivery.infrastructure.orm.commons.integration.PostgresTestContainer
import io.kotest.core.spec.style.StringSpec
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * Only the auto configurations needed for JPA repository integration tests are imported
 * (DataSource + Hibernate EMF + transaction management) instead of the full @EnableAutoConfiguration,
 * keeping the test context to the minimum: no Jackson, web, Liquibase, JMX, task scheduling, etc.
 */
@Configuration
@ImportAutoConfiguration(
    DataSourceAutoConfiguration::class,
    TransactionAutoConfiguration::class,
    JdbcTemplateAutoConfiguration::class,
    HibernateJpaAutoConfiguration::class,
)
@EntityScan("github.com.hukuta94.delivery.infrastructure.orm.spring.model.entity")
@EnableJpaRepositories("github.com.hukuta94.delivery.infrastructure.orm.spring.repository")
open class SpringJpaIntegrationConfig

/**
 * Base for Spring JPA integration specs: a minimal JPA context (shared singleton) bound to the
 * Postgres test container (schema applied by [PostgresTestContainer]). Resolve beans via [context].
 * Mutable tables are truncated before each test. Requires Docker.
 */
abstract class SpringJpaIntegrationSpec : StringSpec() {

    protected val context: ConfigurableApplicationContext = sharedContext

    init {
        beforeTest { PostgresTestContainer.cleanData() }
        afterProject { sharedContext.close() }
    }

    private companion object {
        // One Spring context per test JVM, built once and shared across all specs
        // (building it per spec would be prohibitively slow and would leak connection pools).
        private val sharedContext: ConfigurableApplicationContext by lazy {
            SpringApplicationBuilder(SpringJpaIntegrationConfig::class.java)
                .web(WebApplicationType.NONE)
                .properties(
                    "spring.datasource.url=${PostgresTestContainer.jdbcUrl}",
                    "spring.datasource.username=${PostgresTestContainer.username}",
                    "spring.datasource.password=${PostgresTestContainer.password}",
                    "spring.jpa.hibernate.ddl-auto=none",
                )
                .run()
        }
    }
}
