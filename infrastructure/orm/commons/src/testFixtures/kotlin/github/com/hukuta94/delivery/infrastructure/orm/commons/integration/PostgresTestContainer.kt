package github.com.hukuta94.delivery.infrastructure.orm.commons.integration

import github.com.hukuta94.delivery.infrastructure.orm.commons.CourierStatuses
import github.com.hukuta94.delivery.infrastructure.orm.commons.CourierTransports
import github.com.hukuta94.delivery.infrastructure.orm.commons.MessageStatuses
import github.com.hukuta94.delivery.infrastructure.orm.commons.OrderStatuses
import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.postgresql.ds.PGSimpleDataSource
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.Connection
import java.sql.DriverManager
import javax.sql.DataSource

/**
 * Singleton Postgres container shared across all integration specs in the test JVM.
 * The schema is applied once from the Liquibase changelog of :infrastructure:persistence:migrations.
 * Use [cleanData] (e.g. in beforeEach) to isolate tests; reference tables are kept.
 */
object PostgresTestContainer {

    private const val IMAGE = "postgres:15.3"
    private const val CHANGELOG = "db/changelog/db.changelog-master.xml"

    private val container = PostgreSQLContainer<Nothing>(IMAGE).apply {
        start()
        // Ryuk is disabled (see root build), so stop the container explicitly on JVM exit.
        Runtime.getRuntime().addShutdownHook(Thread { stop() })

        // Apply the schema once from the migrations module changelog (this == the container).
        DriverManager.getConnection(jdbcUrl, username, password).use { connection ->
            val database = DatabaseFactory
                .getInstance()
                .findCorrectDatabaseImplementation(JdbcConnection(connection))

            Liquibase(
                CHANGELOG,
                ClassLoaderResourceAccessor(),
                database
            ).use { liquibase ->
                liquibase.update(Contexts(), LabelExpression())
            }
        }
    }

    val dataSource: DataSource = PGSimpleDataSource().apply {
        setUrl(container.jdbcUrl)
        user = container.username
        password = container.password
    }

    val jdbcUrl: String get() = container.jdbcUrl
    val username: String get() = container.username
    val password: String get() = container.password

    // Reference data seeded by the migrations and Liquibase bookkeeping — never truncated.
    // Everything else (including the seeded couriers) is treated as mutable test data.
    private val PRESERVED_TABLES = setOf(
        CourierStatuses.TABLE_NAME,
        CourierTransports.TABLE_NAME,
        OrderStatuses.TABLE_NAME,
        MessageStatuses.TABLE_NAME,
        // Liquibase bookkeeping tables (framework-managed, no domain constant).
        "databasechangelog",
        "databasechangeloglock",
    )

    private const val SELECT_BASE_TABLES =
        "SELECT table_name FROM information_schema.tables " +
            "WHERE table_schema = 'public' AND table_type = 'BASE TABLE'"

    /**
     * Truncates every mutable table (discovered from the schema) so a newly added table is cleaned
     * automatically; only the reference/bookkeeping tables in [PRESERVED_TABLES] are kept. Note the
     * seeded couriers are removed too — tests insert their own data.
     */
    fun cleanData() {
        dataSource.connection.use { connection ->
            val mutableTables = connection.baseTableNames()
                .filterNot { it.lowercase() in PRESERVED_TABLES }
            if (mutableTables.isEmpty()) {
                return
            }
            connection.createStatement().use { statement ->
                statement.execute("TRUNCATE TABLE ${mutableTables.joinToString()} RESTART IDENTITY CASCADE")
            }
        }
    }

    private fun Connection.baseTableNames(): List<String> =
        prepareStatement(SELECT_BASE_TABLES).use { statement ->
            statement.executeQuery().use { rows ->
                generateSequence { rows.takeIf { it.next() }?.getString("table_name") }.toList()
            }
        }
}
