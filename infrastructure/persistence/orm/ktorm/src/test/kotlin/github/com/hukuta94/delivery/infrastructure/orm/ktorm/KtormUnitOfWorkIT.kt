package github.com.hukuta94.delivery.infrastructure.orm.ktorm

import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.domain.aggregate.courier.freeCourier
import github.com.hukuta94.delivery.infrastructure.orm.commons.integration.PostgresTestContainer
import github.com.hukuta94.delivery.infrastructure.orm.ktorm.repository.KtormCourierRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.ktorm.database.Database
import org.ktorm.support.postgresql.PostgreSqlDialect
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
open class KtormTransactionTestConfig {

    @Bean
    open fun dataSource(): DataSource = PostgresTestContainer.dataSource

    @Bean
    open fun transactionManager(dataSource: DataSource): PlatformTransactionManager =
        DataSourceTransactionManager(dataSource)

    @Bean
    open fun ktormDatabase(dataSource: DataSource): Database =
        Database.connectWithSpringSupport(
            dataSource = dataSource,
            dialect = PostgreSqlDialect()
        )

    @Bean
    open fun unitOfWork(): UnitOfWorkPort = KtormUnitOfWork()

    @Bean
    open fun courierRepository(database: Database): CourierRepositoryPort = KtormCourierRepository(database)
}

/**
 * Verifies A4: [KtormUnitOfWork] (Spring @Transactional over Ktorm's connectWithSpringSupport)
 * actually commits on success and rolls back the whole action on failure.
 */
internal class KtormUnitOfWorkIT : StringSpec() {

    private val context = AnnotationConfigApplicationContext(KtormTransactionTestConfig::class.java)
    private val unitOfWork = context.getBean(UnitOfWorkPort::class.java)
    private val courierRepository = context.getBean(CourierRepositoryPort::class.java)

    init {
        beforeTest { PostgresTestContainer.cleanData() }
        afterSpec { context.close() }

        "commits changes made inside the transaction" {
            val courier = freeCourier()

            unitOfWork.executeInTransaction { courierRepository.add(courier) }

            courierRepository.existsById(courier.id) shouldBe true
        }

        "rolls back every change when the action throws" {
            val courier = freeCourier()

            shouldThrow<IllegalStateException> {
                unitOfWork.executeInTransaction {
                    courierRepository.add(courier)
                    error("forced failure after the insert")
                }
            }

            courierRepository.existsById(courier.id) shouldBe false
        }
    }
}
