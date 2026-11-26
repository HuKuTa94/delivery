package github.com.hukuta94.delivery.e2e

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.util.UUID

/**
 * Reads the state of the running application from its database. Statuses are the codes of the status tables.
 */
class DeliveryDatabase(
    url: String,
    user: String,
    password: String,
) : AutoCloseable {
    private val connection: Connection = DriverManager.getConnection(url, user, password)

    override fun close() {
        connection.close()
    }

    /**
     * @return status of the inbox message with the event of the basket, `null` while there is none
     */
    fun inboxMessageStatus(basketId: UUID): String? = findOne(
        sql = """
            SELECT s.code
            FROM inbox_messages m
            JOIN message_statuses s ON s.id = m.status_id
            WHERE m.payload::jsonb ->> 'basketId' = ?
        """.trimIndent(),
        parameter = basketId.toString(),
    ) { row ->
        row.getString("code")
    }

    /**
     * @return the order with its courier, read in one query, `null` while there is no such order
     */
    fun order(orderId: UUID): OrderRow? = findOne(
        sql = """
            SELECT os.code AS status, o.location, o.courier_id,
                cs.code AS courier_status, c.location AS courier_location
            FROM orders o
            JOIN order_statuses os ON os.id = o.status_id
            LEFT JOIN couriers c ON c.id = o.courier_id
            LEFT JOIN courier_statuses cs ON cs.id = c.status_id
            WHERE o.id = ?
        """.trimIndent(),
        parameter = orderId,
    ) { row ->
        OrderRow(
            status = row.getString("status"),
            location = row.getString("location"),
            courier = row.courier(),
        )
    }

    private fun <T> findOne(
        sql: String,
        parameter: Any,
        map: (ResultSet) -> T,
    ): T? = connection.prepareStatement(sql).use { statement ->
        statement.setObject(1, parameter)
        statement.executeQuery().use { rows ->
            if (rows.next()) map(rows) else null
        }
    }

    private fun ResultSet.courier(): CourierRow? = getObject("courier_id", UUID::class.java)?.let { id ->
        CourierRow(
            id = id,
            status = getString("courier_status"),
            location = getString("courier_location"),
        )
    }

    data class OrderRow(
        val status: String,
        val location: String,
        val courier: CourierRow?,
    )

    data class CourierRow(
        val id: UUID,
        val status: String,
        val location: String,
    )
}
