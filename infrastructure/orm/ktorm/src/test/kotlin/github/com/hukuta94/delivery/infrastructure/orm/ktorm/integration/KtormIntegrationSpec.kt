package github.com.hukuta94.delivery.infrastructure.orm.ktorm.integration

import github.com.hukuta94.delivery.infrastructure.orm.commons.integration.PostgresTestContainer
import io.kotest.core.spec.style.StringSpec
import org.ktorm.database.Database

/**
 * Base for Ktorm integration specs: provides a [Database] bound to the shared Postgres
 * test container and truncates mutable tables before each test. Requires Docker.
 */
abstract class KtormIntegrationSpec : StringSpec() {

    protected val database: Database = sharedDatabase

    init {
        beforeTest { PostgresTestContainer.cleanData() }
    }

    private companion object {
        // One Database per test JVM, shared across all specs (the container itself is a singleton).
        val sharedDatabase: Database by lazy {
            Database.connect(PostgresTestContainer.dataSource)
        }
    }
}
