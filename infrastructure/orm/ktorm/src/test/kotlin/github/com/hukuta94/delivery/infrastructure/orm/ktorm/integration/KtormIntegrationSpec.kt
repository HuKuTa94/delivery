package github.com.hukuta94.delivery.infrastructure.orm.ktorm.integration

import github.com.hukuta94.delivery.infrastructure.orm.commons.integration.PostgresTestContainer
import io.kotest.core.spec.style.StringSpec
import org.ktorm.database.Database
import org.ktorm.dsl.count
import org.ktorm.dsl.from
import org.ktorm.dsl.map
import org.ktorm.dsl.select
import org.ktorm.schema.Table

/**
 * Base for Ktorm integration specs: provides a [Database] bound to the shared Postgres
 * test container and truncates mutable tables before each test. Requires Docker.
 */
abstract class KtormIntegrationSpec : StringSpec() {

    init {
        beforeTest { PostgresTestContainer.cleanData() }
    }

    protected val database: Database = sharedDatabase

    protected fun count(table: Table<Nothing>) = database
        .from(table)
        .select(count())
        .map { row -> row.getInt(1) }
        .single()

    private companion object {
        // One Database per test JVM, shared across all specs (the container itself is a singleton).
        val sharedDatabase: Database by lazy {
            Database.connect(PostgresTestContainer.dataSource)
        }
    }
}
