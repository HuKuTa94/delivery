package github.com.hukuta94.delivery.infrastructure.orm.commons

import github.com.hukuta94.delivery.core.domain.aggregate.courier.CourierStatus
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Transport
import github.com.hukuta94.delivery.core.domain.aggregate.order.OrderStatus
import github.com.hukuta94.delivery.infrastructure.orm.commons.integration.PostgresTestContainer
import github.com.hukuta94.delivery.infrastructure.orm.commons.integration.concurrency.StartGate
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import java.sql.SQLException
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

private const val UNIQUE_VIOLATION = "23505"
private const val CHECK_VIOLATION = "23514"
private const val ROUNDS = 20
private const val THREADS = 4

/**
 * Invariants of the assignment kept by the schema itself (changeset add_order_courier_constraints), so they hold
 * whatever the number of threads or instances writing orders and whichever adapter writes them.
 * Every statement runs on a connection of its own in its own transaction, as concurrent writers do.
 */
internal class OrderCourierConstraintsIT : StringSpec({

    beforeTest { PostgresTestContainer.cleanData() }

    "a courier with an assigned order can't be assigned a second one" {
        // Given
        val courierId = insertCourier()
        insertOrder(
            status = OrderStatus.ASSIGNED,
            courierId = courierId,
        )

        // When
        val error = shouldThrow<SQLException> {
            insertOrder(
                status = OrderStatus.ASSIGNED,
                courierId = courierId,
            )
        }

        // Then
        error.sqlState shouldBe UNIQUE_VIOLATION
    }

    "a courier whose order is completed can be assigned the next one" {
        // Given
        val courierId = insertCourier()
        insertOrder(
            status = OrderStatus.COMPLETED,
            courierId = courierId,
        )

        // When
        val outcome = runCatching {
            insertOrder(
                status = OrderStatus.ASSIGNED,
                courierId = courierId,
            )
        }

        // Then
        outcome.shouldBeSuccess()
    }

    "a created order can't have a courier" {
        // Given
        val courierId = insertCourier()

        // When
        val error = shouldThrow<SQLException> {
            insertOrder(
                status = OrderStatus.CREATED,
                courierId = courierId,
            )
        }

        // Then
        error.sqlState shouldBe CHECK_VIOLATION
    }

    "an assigned or a completed order can't be without a courier" {
        // Given
        val statuses = listOf(OrderStatus.ASSIGNED, OrderStatus.COMPLETED)

        // When
        val errors = statuses.map { status ->
            shouldThrow<SQLException> {
                insertOrder(
                    status = status,
                    courierId = null,
                )
            }
        }

        // Then
        errors.map { it.sqlState } shouldBe listOf(CHECK_VIOLATION, CHECK_VIOLATION)
    }

    "of the orders assigned to one courier at the same moment only one is stored" {
        // Given: every round a courier and a created order per thread, see assignToOneCourierAtOnce()

        // When
        val rounds = List(ROUNDS) { assignToOneCourierAtOnce() }

        // Then
        assertSoftly {
            rounds.map { outcomes -> outcomes.count { it.isSuccess } }.toSet() shouldBe setOf(1)
            rounds.flatten()
                .mapNotNull { (it.exceptionOrNull() as? SQLException)?.sqlState }
                .toSet() shouldBe setOf(UNIQUE_VIOLATION)
        }
    }
})

private fun assignToOneCourierAtOnce(): List<Result<Unit>> {
    val courierId = insertCourier()
    val orderIds = List(THREADS) {
        insertOrder(
            status = OrderStatus.CREATED,
            courierId = null,
        )
    }
    return StartGate.run(
        threads = THREADS,
        timeout = 30.seconds,
    ) { thread ->
        execute(
            sql = "UPDATE orders SET status_id = ?, courier_id = ? WHERE id = ?",
            parameters = listOf(OrderStatus.ASSIGNED.id, courierId, orderIds[thread]),
        )
    }
}

private fun insertCourier(): UUID = UUID.randomUUID().also { id ->
    execute(
        sql = "INSERT INTO couriers (id, name, location, status_id, transport_id) VALUES (?, 'Courier', '1,1', ?, ?)",
        parameters = listOf(id, CourierStatus.FREE.id, Transport.PEDESTRIAN.id),
    )
}

private fun insertOrder(
    status: OrderStatus,
    courierId: UUID?,
): UUID = UUID.randomUUID().also { id ->
    execute(
        sql = "INSERT INTO orders (id, status_id, courier_id, location) VALUES (?, ?, ?, '1,1')",
        parameters = listOf(id, status.id, courierId),
    )
}

private fun execute(
    sql: String,
    parameters: List<Any?>,
) {
    PostgresTestContainer.dataSource.connection.use { connection ->
        connection.prepareStatement(sql).use { statement ->
            parameters.forEachIndexed { index, parameter -> statement.setObject(index + 1, parameter) }
            statement.executeUpdate()
        }
    }
}
